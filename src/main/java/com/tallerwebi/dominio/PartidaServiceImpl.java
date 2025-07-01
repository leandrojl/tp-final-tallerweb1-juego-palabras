package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.AciertoService;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.model.*;

import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;

import com.tallerwebi.dominio.interfaceRepository.RondaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

import java.util.Comparator;
import java.util.List;

import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


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

    private final Map<Long, RondaDto> definicionesPorPartida = new HashMap<>();

    @Autowired
    public PartidaServiceImpl(SimpMessagingTemplate simpMessagingTemplate,
                              PartidaRepository partidaRepository,
                              RondaService rondaService,
                              RondaRepository rondaRepositorio,
                              UsuarioPartidaRepository usuarioPartidaRepository,
                              AciertoService aciertoService,
                              UsuarioPartidaService usuarioPartidaService
    ) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.partidaRepository = partidaRepository;
        this.rondaService = rondaService;
        this.aciertoService = aciertoService;
        this.usuarioPartidaService = usuarioPartidaService;
        this.usuarioPartidaRepository = usuarioPartidaRepository;
        this.rondaRepositorio = rondaRepositorio;
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
        //   ---- si acerto verificar si acertaron todos y finalizar ronda timer -----  //
        System.out.println("procesar intento aqui estoy ");

        Long partidaId = intento.getIdPartida();
        System.out.println("partida: "+partidaId);

        Long usuarioId = intento.getUsuarioId();
        System.out.println("usuarioId "+usuarioId);

        String intentoTexto = intento.getIntentoPalabra();
        System.out.println("intentoTexto "+intentoTexto);

        // === Obtener Partida
        System.out.println("procesar intento aqui estoy3 ");

        Partida partida = partidaRepository.buscarPorId(partidaId);
        System.out.println("PARTIDA NULA ");

        if (partida == null) {
            throw new IllegalArgumentException("Partida no encontrada con ID: " + partidaId);
        }

        // === Obtener Ronda actual
        Ronda ronda = rondaService.obtenerUltimaRondaDePartida(partidaId);
        Long rondaId = ronda.getId();
        if (ronda == null) {
            System.out.println("Ronda NULA ");
            throw new IllegalStateException("No hay una ronda activa.");
        } else if (ronda.getEstado().equals(Estado.FINALIZADA)) {
            throw new IllegalStateException("Ronda finalizada.");
        }
        System.out.println("Ronda== " + rondaId);

        // === Comparar intento con palabra correcta
        String palabraCorrecta = ronda.getPalabra().getDescripcion();

        boolean esCorrecto = intentoTexto.equalsIgnoreCase(palabraCorrecta);

        // Armamos el ResultadoDto
        //Usuario usuario = usuarioPartidaService.obtenerUsuarioPorUsuarioIdYPartidaId(usuarioId, partidaId);
        //System.out.println("Usuario encontrado: " + usuario + " nombreUsuario=" + (usuario != null ? usuario.getNombreUsuario() : "null"));
        ResultadoIntentoDto resultadoPrivado = new ResultadoIntentoDto();
        resultadoPrivado.setJugador(nombre);

        System.out.println("ES CORRECTO = " + esCorrecto + " " + palabraCorrecta + " " + palabraCorrecta);

        ResultadoIntentoDto resultadoPublico = new ResultadoIntentoDto();
        resultadoPublico.setJugador(nombre);

        if (esCorrecto) {
            // Verificar si ya había acertado ===
            boolean yaAcerto = aciertoService.jugadorYaAcerto(usuarioId, rondaId);
            if (!yaAcerto) {
                // Registrar acierto y calcular puntos ===
                int puntos = aciertoService.registrarAcierto(usuarioId, rondaId);

                // Sumar puntos en UsuarioPartida ===
                // usuarioPartidaService.sumarPuntos(usuarioId, partidaId, puntos);

                // Al jugador que acertó
                resultadoPrivado.setPalabraCorrecta(intentoTexto);
                resultadoPrivado.setCorrecto(true);
                resultadoPrivado.setPalabraIncorrecta("");
                simpMessagingTemplate.convertAndSendToUser(nombre, "/queue/resultado", resultadoPrivado);

                // Al resto: mensaje "pepito acertó"
                resultadoPublico.setCorrecto(true);
                resultadoPublico.setMensaje("✅ " + nombre + " acertó la palabra");
                simpMessagingTemplate.convertAndSend(
                        "/topic/mostrarIntento/" + partidaId,
                        resultadoPublico
                );

            }

        } else {
            System.out.println("PROCESAR INTENTO LLEGO HASTA AQUI4= " + intento.getIntentoPalabra());
            // A todos (incluido el que lo envió), la palabra en rojo
            resultadoPublico.setCorrecto(false);
            resultadoPublico.setPalabraIncorrecta(intentoTexto);
            simpMessagingTemplate.convertAndSend(
                    "/topic/mostrarIntento/" + partidaId,
                    resultadoPublico
            );
        }
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

    @Override
    public synchronized  RondaDto iniciarNuevaRonda(Long partidaId) {
        Partida partida = partidaRepository.buscarPorId(partidaId);
        if (partida == null) {
            throw new IllegalArgumentException("No existe la partida con ID: " + partidaId);
        }

        // Validación para no crear dos rondas activas
        Ronda ultima = rondaService.obtenerUltimaRondaDePartida(partidaId);
        System.out.println("SOY LA ULTIMA RONDA PAAA?????"+ultima);
        if (ultima != null && ultima.getEstado() == Estado.EN_CURSO) {
            System.out.println("Ya hay una ronda activa...");

            Palabra palabra = ultima.getPalabra();
            String definicionTexto = palabra.getDefiniciones().stream()
                    .findFirst()
                    .map(Definicion::getDefinicion)
                    .orElse("Definición no disponible");

            List<UsuarioPartida> usuariosEnPartida = usuarioPartidaRepository.obtenerUsuarioPartidaPorPartida(partidaId);

            List<JugadorPuntajeDto> jugadoresDto = usuariosEnPartida.stream()
                    .map(up -> new JugadorPuntajeDto(up.getUsuario().getNombreUsuario(), up.getPuntaje()))
                    .collect(Collectors.toList());

            RondaDto dto = new RondaDto();
            dto.setPalabra(palabra.getDescripcion());
            dto.setDefinicionTexto(definicionTexto);
            dto.setNumeroDeRonda(ultima.getNumeroDeRonda());
            dto.setJugadores(jugadoresDto);

            simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId, dto);
            simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId,
                    new MensajeTipoRanking("actualizar-puntajes", jugadoresDto));

            return dto;
        }

        String idioma = partida.getIdioma();
        Ronda ronda = rondaService.crearRonda(partidaId, idioma);
        Palabra palabra = ronda.getPalabra();
        String definicionTexto = palabra.getDefiniciones().stream()
                .findFirst()
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
        System.out.println("palabra== " + palabra.getDescripcion());
        System.out.println("Definicion== " + definicionTexto);
        System.out.println("Ronda== " + ronda.getId());


        simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId, dto);

        MensajeTipoRanking mensaje = new MensajeTipoRanking("actualizar-puntajes", jugadoresDto);
        simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId, mensaje);

        return dto;
    }

    @Override
    public DefinicionDto avanzarRonda(MensajeAvanzarRondaDTO dto) {
        Long partidaId = dto.getIdPartida();
        Long rondaId = dto.getIdRonda();

        Partida partida = partidaRepository.buscarPorId(partidaId);
        Ronda rondaActual = rondaRepositorio.obtenerUltimaRondaDePartida(partidaId);

        // Marcar ronda anterior como terminada
        rondaActual.setEstado(Estado.FINALIZADA);
        rondaRepositorio.actualizar(rondaActual);

        // ¿Terminó la partida?
        if (esUltimaRonda(partidaId)) {
            partida.setEstado(Estado.FINALIZADA);
            partidaRepository.actualizarEstado(partidaId, Estado.FINALIZADA);
            return null;
        }

        // Crear nueva ronda
        Ronda nuevaRonda = rondaService.crearRonda(partidaId, partida.getIdioma());
        Palabra palabra = nuevaRonda.getPalabra();

        String definicionTexto = palabra.getDefiniciones().stream()
                .findFirst()
                .map(Definicion::getDefinicion)
                .orElse("Definición no disponible");

        DefinicionDto dtoRespuesta = new DefinicionDto();
        dtoRespuesta.setPalabra(palabra.getDescripcion());
        dtoRespuesta.setDefinicionTexto(definicionTexto);
        dtoRespuesta.setNumeroDeRonda(nuevaRonda.getNumeroDeRonda());

        simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId, dtoRespuesta);
        return dtoRespuesta;
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

    Map<Long, RondaDto> obtenerMapaDefinicionesParaTest() {
        return definicionesPorPartida;
    }

}
