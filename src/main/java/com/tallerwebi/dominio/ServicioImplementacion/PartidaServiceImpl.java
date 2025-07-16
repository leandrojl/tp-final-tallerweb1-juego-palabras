package com.tallerwebi.dominio.ServicioImplementacion;

import com.tallerwebi.dominio.RondaTimerManager;
import com.tallerwebi.dominio.DTO.*;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.*;

import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;

import com.tallerwebi.dominio.interfaceRepository.RondaRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

import java.util.*;

import java.util.concurrent.*;
import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;



@Service
@Transactional
public class PartidaServiceImpl implements PartidaService {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    private final ScheduledExecutorService timerRonda;
    private ScheduledFuture<?> finalizarRondaPorTimer;

    @Autowired
    private final PartidaRepository partidaRepository;
    @Autowired
    private final RondaService rondaService;
    @Autowired
    private final AciertoService aciertoService;
    @Autowired
    private final UsuarioPartidaService usuarioPartidaService;
    @Autowired
    private final UsuarioPartidaRepository usuarioPartidaRepository;
    @Autowired
    private final RondaRepository rondaRepositorio;
    @Autowired
    private RondaTimerManager rondaTimerManager;
    @Autowired
    private GeminiBotService botService;


    private final Map<String, ScheduledFuture<?>> usuariosBloqueados = new ConcurrentHashMap<>();

    private final Map<Long, RondaDto> definicionesPorPartida = new HashMap<>();
    private final ConcurrentHashMap<Long, Object> locks = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> tareasPorPartida = new ConcurrentHashMap<>();

    @Autowired
    public PartidaServiceImpl(SimpMessagingTemplate simpMessagingTemplate,
                              PartidaRepository partidaRepository,
                              RondaService rondaService,
                              RondaRepository rondaRepositorio,
                              UsuarioPartidaRepository usuarioPartidaRepository,
                              AciertoService aciertoService,
                              UsuarioPartidaService usuarioPartidaService,
                              RondaTimerManager rondaTimerManager,
                              GeminiBotService botService
    ) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.partidaRepository = partidaRepository;
        this.rondaService = rondaService;
        this.aciertoService = aciertoService;
        this.usuarioPartidaService = usuarioPartidaService;
        this.usuarioPartidaRepository = usuarioPartidaRepository;
        this.rondaRepositorio = rondaRepositorio;
        this.rondaTimerManager=rondaTimerManager;
        this.botService=botService;
        //this.timerRonda = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 10);
        // el newSingle crea un solo hilo de tarea, que retrasaria si hay multiples partidas, en cambio el newSchedule genera
        //un pool de hilos reutilizables de manera que funciona si se juega mas de una partida en simultaneo(el numero
        //multiplicador genera mas hilos, pero tambien consume mas recursos,

      //
          this.timerRonda = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public Partida obtenerPartidaPorId(Long partidaId) {
        return null;
    }

    @Override
    public void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        simpMessagingTemplate.convertAndSendToUser(nombreUsuario, "/queue/paraTest", new MensajeEnviadoDTO(nombreUsuario,
                mensaje));
    }

    @Override
    public void procesarIntento(DtoIntento intento, String nombre) {

        Long partidaId = intento.getIdPartida();
        Long idUsuario = intento.getIdUsuario();
        String intentoTexto = intento.getIntentoPalabra();
        Partida partida = partidaRepository.buscarPorId(partidaId);

        if (partida == null) {
            throw new IllegalArgumentException("Partida no encontrada con ID: " + partidaId);
        }

        Ronda ronda = rondaService.obtenerUltimaRondaDePartida(partidaId);
        Long rondaId = ronda.getId();
        if (ronda == null) {
            System.out.println("Ronda NULA ");
            throw new IllegalStateException("No hay una ronda activa.");
        } else if (ronda.getEstado().equals(Estado.FINALIZADA)) {
            throw new IllegalStateException("Ronda finalizada.");
        }

        String palabraCorrecta = ronda.getPalabra().getDescripcion();
        boolean esCorrecto = intentoTexto.equalsIgnoreCase(palabraCorrecta);
        ResultadoIntentoDto resultadoPrivado = new ResultadoIntentoDto();
        resultadoPrivado.setJugador(nombre);
        ResultadoIntentoDto resultadoPublico = new ResultadoIntentoDto();
        resultadoPublico.setJugador(nombre);

        if (esCorrecto) {
            boolean yaAcerto = aciertoService.jugadorYaAcerto(idUsuario, rondaId);
            if (!yaAcerto) {
                 int puntos = aciertoService.registrarAcierto(idUsuario, rondaId);
                 usuarioPartidaService.sumarPuntos(idUsuario, partidaId, puntos);
                 resultadoPublico.setCorrecto(true);
                 resultadoPublico.setMensaje(nombre + " acertó la palabra");
                 simpMessagingTemplate.convertAndSend(
                        "/topic/mostrarIntento/" + partidaId,
                        resultadoPublico

                );

                List<UsuarioPartida> jugadores = usuarioPartidaRepository.buscarPorPartida(partidaId);
                List<JugadorPuntajeDto> jugadoresDto = jugadores.stream()
                        .map(up -> new JugadorPuntajeDto(up.getUsuario().getNombreUsuario(), up.getPuntaje()))
                        .collect(Collectors.toList());

                simpMessagingTemplate.convertAndSend(
                        "/topic/verRanking/" + partidaId,
                        new MensajeTipoRanking("actualizar-puntajes", jugadoresDto)
                );
            finalizarRondaEnCasoDeQueTodosAcertaron(partidaId,rondaId);
            }

        } else {

            resultadoPublico.setCorrecto(false);
            resultadoPublico.setPalabraIncorrecta(intentoTexto);
            resultadoPublico.setJugador(nombre);
            resultadoPublico.setMensaje(null);
            simpMessagingTemplate.convertAndSend(
                    "/topic/mostrarIntento/" + partidaId,
                    resultadoPublico
            );
        }
    }



    @Transactional
    @Override
    public RondaDto iniciarNuevaRonda(Long partidaId) {
        Object lock = locks.computeIfAbsent(partidaId, id -> new Object());
        boolean permiteBot = false;
        RondaDto dtoRonda;

        synchronized (lock) {
            Partida partida = partidaRepository.buscarPorId(partidaId);
            permiteBot = partida.isPermiteBot();
            if (partida == null) {
                throw new IllegalArgumentException("No existe la partida con ID: " + partidaId);
            }

            Ronda ultima = rondaService.obtenerUltimaRondaDePartida(partidaId);
            if (ultima != null && ultima.getEstado() == Estado.EN_CURSO) {
                return construirDtoDesdeRondaExistente(ultima, partidaId);
            }

            String idioma = partida.getIdioma();
            Ronda nueva = rondaService.crearRonda(partidaId, idioma);
            rondaTimerManager.agendarFinalizacionRonda(partidaId, 60);

            dtoRonda = construirDtoDesdeRondaExistente(nueva, partidaId);
        }

        String definicion = dtoRonda.getDefinicionTexto();
        if(permiteBot) CompletableFuture.runAsync(() ->
                botService.generateAndSubmitGuess(definicion, partidaId)
        );

        return dtoRonda;
    }



    @Transactional
    @Override
    public DefinicionDto avanzarRonda(MensajeAvanzarRondaDTO dto) {
        Long partidaId = dto.getIdPartida();
        if(!dto.isTiempoAgotado()) rondaTimerManager.cancelarTarea(partidaId);
        Long rondaId = dto.getIdRonda();

        Partida partida = partidaRepository.buscarPorId(partidaId);
        Ronda rondaActual = rondaRepositorio.obtenerUltimaRondaDePartida(partidaId);
        rondaActual.setEstado(Estado.FINALIZADA);
        rondaRepositorio.actualizar(rondaActual);


        if (esUltimaRonda(partidaId)) {
            partida.setEstado(Estado.FINALIZADA);
            partidaRepository.actualizarEstado(partidaId, Estado.FINALIZADA);
            usuarioPartidaService.finalizarPartida(partidaId, Estado.FINALIZADA);
            simpMessagingTemplate.convertAndSend(
                    "/topic/redirigir",
                    "/spring/juego/vistaFinalJuego?partidaId=" + partidaId
            );
        return null;
        }
        enviarMensajeTimerInicioNuevaRonda(partidaId);
                iniciarNuevaRonda(partidaId);
        return null;
    }



    @Override
    public boolean esUltimaRonda(Long idPartida) {
        Partida partida = partidaRepository.buscarPorId(idPartida);
        Ronda ultimaRonda = rondaRepositorio.obtenerUltimaRondaDePartida(idPartida);
        return ultimaRonda.getNumeroDeRonda() >= partida.getRondasTotales();
    }

    @Override
    public void enviarRankingFinal(Long idPartida) {

        List<UsuarioPartida> jugadores = usuarioPartidaRepository.buscarPorPartida(idPartida);

        List<RankingDTO> ranking = jugadores.stream()
                .map(up -> new RankingDTO(up.getUsuario().getNombreUsuario(), up.getPuntaje()))
                .sorted(Comparator.comparingInt(RankingDTO::getPuntaje).reversed())
                .collect(Collectors.toList());


        simpMessagingTemplate.convertAndSend("/topic/vistaFinal", ranking);
    }
    @Transactional
    @Override
    public List<RankingDTO> obtenerRanking(Long partidaId) {
        List<UsuarioPartida> jugadores = usuarioPartidaRepository.buscarPorPartida(partidaId);

        return jugadores.stream()
                .map(up -> new RankingDTO(up.getUsuario().getNombreUsuario(), up.getPuntaje()))
                .sorted(Comparator.comparingInt(RankingDTO::getPuntaje).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Serializable crearPartida(Partida nuevaPartida) {
        return partidaRepository.crearPartida(nuevaPartida);
    }

    @Override
    public Serializable crearPartida(CrearPartidaDTO partidaDTO, Long creadorId) {
        Partida nuevaPartida = new Partida(
                partidaDTO.getNombre(),
                partidaDTO.getIdioma(),
                partidaDTO.isPermiteComodin(),
                partidaDTO.getRondasTotales(),
                partidaDTO.getMaximoJugadores(),
                partidaDTO.getMinimoJugadores(),
                Estado.EN_ESPERA,
                creadorId,
                partidaDTO.isPermiteBot()
        );
        return partidaRepository.crearPartida(nuevaPartida);
    }

    public ScheduledFuture<?> getFinalizarRondaPorTimer() {
        return finalizarRondaPorTimer;
    }

    @Override
    public RondaDto obtenerPalabraYDefinicionDeRondaActual(Long partidaId) {
        return definicionesPorPartida.get(partidaId);
    }

    @Override
    public void finalizarRonda(Ronda ronda) {

    }

    @Override
    public void cancelarPartidaDeUsuario(Long idUsuario, Long idPartida) {
        partidaRepository.cancelarPartidaDeUsuario(idUsuario, idPartida);
    }

    @Override
    public boolean verificarSiEsElCreadorDePartida(Long idUsuario, Long idPartida) {
        return partidaRepository.verificarSiEsElCreadorDePartida(idUsuario, idPartida);
    }

    @Override
    public Estado verificarEstadoDeLaPartida(Long idPartida) {
        return partidaRepository.verificarEstadoDeLaPartida(idPartida);
    }


    public RondaDto construirDtoDesdeRondaExistente(Ronda ronda, Long partidaId) {
        Palabra palabra = ronda.getPalabra();
        String definicionTexto;
        Hibernate.initialize(palabra.getDefiniciones());

            definicionTexto = palabra.getDefiniciones().stream()
                    .findAny()
                    .map(Definicion::getDefinicion)
                    .orElse("Definición no disponible");

        List<UsuarioPartida> usuariosEnPartida = usuarioPartidaRepository.obtenerUsuarioPartidaPorPartida(partidaId);

        List<JugadorPuntajeDto> jugadoresDto = usuariosEnPartida.stream()
                .map(up -> new JugadorPuntajeDto(up.getUsuario().getNombreUsuario(), up.getPuntaje()))
                .collect(Collectors.toList());


        RondaDto dto = new RondaDto();
        dto.setPalabra(palabra.getDescripcion());
        dto.setDefinicionTexto(definicionTexto);
        dto.setNumeroDeRonda(ronda.getNumeroDeRonda());
        dto.setJugadores(jugadoresDto);

        simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId, dto);
        simpMessagingTemplate.convertAndSend("/topic/verRanking/" + partidaId,
                new MensajeTipoRanking("actualizar-puntajes", jugadoresDto));

        return dto;
    }



    @Override
    @Transactional // La operación debe ser atómica.
    public void cancelarPartidaSiEsCreador(Long idUsuario, Long idPartida) {
       if (verificarSiEsElCreadorDePartida(idUsuario, idPartida)) {
            Partida partida = partidaRepository.buscarPartidaPorId(idPartida);
            if (partida != null && partida.getEstado() == Estado.EN_ESPERA) {
                partidaRepository.modificarEstadoPartida(partida, Estado.CANCELADA);
                usuarioPartidaService.cancelarPartidaDeUsuario(idUsuario, idPartida);
            }
        }
    }

    @Override
    public String obtenerNombrePartidaPorId(Long idPartida) {
        return partidaRepository.obtenerNombrePartidaPorId(idPartida);
    }
    @Override
    public void procesarIntento1(DtoIntento intento) {
        ResultadoIntentoDto resultado = new ResultadoIntentoDto();
        //=================== HARCODEADO =============================== //
        Boolean correcta = false;
        if (correcta) {
            resultado.setPalabraCorrecta(intento.getIntentoPalabra());
            resultado.setPalabraIncorrecta("");
            resultado.setJugador("pepito");
            resultado.setCorrecto(true);
            simpMessagingTemplate.convertAndSendToUser("hla", "/queue/resultado", resultado);
        } else {
            resultado.setPalabraCorrecta("");
            resultado.setPalabraIncorrecta(intento.getIntentoPalabra());
            resultado.setJugador("pepito");
            resultado.setCorrecto(false);
            simpMessagingTemplate.convertAndSend(
                    "/topic/mostrarIntento/" + intento.getIdPartida(),
                    resultado);
        }
    }

    private void enviarMensajeTimerInicioNuevaRonda(Long partidaId) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/timerInicioRonda/" + partidaId, "La próxima ronda iniciará en:");
            Thread.sleep(1000); // Espera 1 segundo antes de empezar la cuenta
            for (int i = 5; i > 0; i--) {
                simpMessagingTemplate.convertAndSend("/topic/timerInicioRonda/" + partidaId, String.valueOf(i));
                Thread.sleep(1000); // Espera 1 segundo entre cada número
            }
            simpMessagingTemplate.convertAndSend("/topic/timerInicioRonda/" + partidaId, "¡Comienza!");




        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    private void finalizarRondaEnCasoDeQueTodosAcertaron(Long partidaId, Long rondaId) {
        int cantAciertos = aciertoService.cantidadDeAciertosEnLaRonda(rondaId);
        int cantJugadores = usuarioPartidaService.cantidadDeJugadoresActivosEnPartida(partidaId);
        if(cantAciertos==cantJugadores){
            MensajeAvanzarRondaDTO dto= new MensajeAvanzarRondaDTO(partidaId,rondaId, false);
            avanzarRonda(dto);
        }
    }

    public void activarComodin(Long idPartida, Long idUsuario, String nombreUsuario) {
        UsuarioPartida usuarioPartida = usuarioPartidaRepository.obtenerUsuarioPartida(idUsuario, idPartida);

        if (usuarioPartida == null) return;
        if (usuarioPartida.isComodinUsado()) return;

        Ronda rondaActual = rondaRepositorio.obtenerUltimaRondaDePartida(idPartida);
        if (rondaActual == null || rondaActual.getEstado() == Estado.FINALIZADA) return;

        String palabra = rondaActual.getPalabra().getDescripcion();
        Random random = new Random();
        int indiceLetra = random.nextInt(palabra.length());
        char letra = palabra.charAt(indiceLetra);

        // Marcar comodín como usado
        usuarioPartida.setComodinUsado(true);
        usuarioPartidaRepository.actualizar(usuarioPartida);

        // Enviar al jugador solo la letra descubierta
        LetraComodinDto letraDto = new LetraComodinDto(indiceLetra, String.valueOf(letra));
        simpMessagingTemplate.convertAndSendToUser(nombreUsuario, "/queue/comodin", letraDto);
    }


    @Override
    public void obtenerUsuariosParaBloquear(Long idPartida, String nombreUsuario) {
        List<UsuarioPartida> usuariosEnPartida = usuarioPartidaRepository.obtenerUsuarioPartidaPorPartida(idPartida);

        List<String> nombresUsuarios = usuariosEnPartida.stream()
                .map(up -> up.getUsuario().getNombreUsuario())
                .filter(nombre -> !nombre.equals(nombreUsuario)) // Excluir al usuario actual
                .collect(Collectors.toList());

        ListaUsuariosDTO listaDto = new ListaUsuariosDTO(nombresUsuarios);
        simpMessagingTemplate.convertAndSendToUser(nombreUsuario, "/queue/listaUsuarios", listaDto);
    }

    @Override
    public void bloquearUsuario(Long idPartida, Long idUsuario, String nombreUsuario, String usuarioABloquear) {
        UsuarioPartida usuarioPartida = usuarioPartidaRepository.obtenerUsuarioPartida(idUsuario, idPartida);

        if (usuarioPartida == null) return;
        if (usuarioPartida.isComodinBloqueoUsado()) return; // Añadir este campo a la entidad

        Ronda rondaActual = rondaRepositorio.obtenerUltimaRondaDePartida(idPartida);
        if (rondaActual == null || rondaActual.getEstado() == Estado.FINALIZADA) return;

        // Marcar comodín como usado
        usuarioPartida.setComodinBloqueoUsado(true);
        usuarioPartidaRepository.actualizar(usuarioPartida);

        // Crear clave única para el usuario bloqueado
        String claveBloqueo = idPartida + "-" + usuarioABloquear;

        // Cancelar bloqueo anterior si existe
        ScheduledFuture<?> bloqueoAnterior = usuariosBloqueados.get(claveBloqueo);
        if (bloqueoAnterior != null && !bloqueoAnterior.isDone()) {
            bloqueoAnterior.cancel(false);
        }

        // Notificar al usuario bloqueado
        BloqueoDto bloqueoDto = new BloqueoDto(usuarioABloquear, 10,
                nombreUsuario + " te ha bloqueado por 10 segundos");
        simpMessagingTemplate.convertAndSendToUser(usuarioABloquear, "/queue/bloqueo", bloqueoDto);

        // Notificar al resto de usuarios
        BloqueoDto notificacionPublica = new BloqueoDto(usuarioABloquear, 10,
                nombreUsuario + " ha bloqueado a " + usuarioABloquear + " por 10 segundos");
        simpMessagingTemplate.convertAndSend("/topic/notificacionBloqueo/" + idPartida, notificacionPublica);

        // Programar desbloqueo automático
        ScheduledFuture<?> tareaDesbloqueo = timerRonda.schedule(() -> {
            BloqueoDto desbloqueoDto = new BloqueoDto(usuarioABloquear, 0, "Tu bloqueo ha terminado");
            simpMessagingTemplate.convertAndSendToUser(usuarioABloquear, "/queue/desbloqueo", desbloqueoDto);

            // Notificar al resto
            BloqueoDto notificacionDesbloqueo = new BloqueoDto(usuarioABloquear, 0,
                    usuarioABloquear + " ya puede escribir nuevamente");
            simpMessagingTemplate.convertAndSend("/topic/notificacionBloqueo/" + idPartida, notificacionDesbloqueo);

            usuariosBloqueados.remove(claveBloqueo);
        }, 10, TimeUnit.SECONDS);

        usuariosBloqueados.put(claveBloqueo, tareaDesbloqueo);
    }


}
