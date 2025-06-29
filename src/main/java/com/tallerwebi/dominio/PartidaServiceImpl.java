package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.PuntajeService;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.infraestructura.RondaRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


@Service
@Transactional
public class PartidaServiceImpl implements PartidaService {

    SimpMessagingTemplate simpMessagingTemplate;

    private final ScheduledExecutorService timerRonda;

    private ScheduledFuture<?> finalizarRondaPorTimer;

    @Autowired
    private final PartidaRepository partidaRepository;
    @Autowired
    private final RondaService rondaService;
    @Autowired
    private final RondaRepository rondaRepositorio;
    @Autowired
    private final UsuarioPartidaRepository usuarioPartidaRepository;
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Autowired
    private SessionFactory sessionFactory2;
    @Autowired
    private PuntajeService puntajeService;

    @Autowired
    public PartidaServiceImpl(SimpMessagingTemplate simpMessagingTemplate,
                              PartidaRepository partidaRepository,
                              RondaService rondaService,
                              RondaRepository rondaRepositorio,
                              UsuarioPartidaRepository usuarioPartidaRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.partidaRepository = partidaRepository;
        this.rondaService = rondaService;
        this.rondaRepositorio = rondaRepositorio;
        this.usuarioPartidaRepository = usuarioPartidaRepository;
        // 👇 Inicializamos DIRECTAMENTE aquí
        this.timerRonda = Executors.newSingleThreadScheduledExecutor();

    }

    @Override
    public void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        simpMessagingTemplate.convertAndSendToUser(nombreUsuario,"/queue/paraTest",new MensajeEnviadoDTO(nombreUsuario,
                mensaje));
    }

    @Override
    public ResultadoIntentoDto procesarIntento(DtoIntento intento, String nombreJugador) {
        Long partidaId = intento.getPartidaId();
        String textoIntentado = intento.getIntentoTexto();

        // 1. Obtener la partida
        Partida partida = partidaRepository.buscarPorId(partidaId);
        if (partida == null) {
            throw new IllegalArgumentException("Partida no encontrada con ID: " + partidaId);
        }

        // 2. Obtener la ronda actual (esto depende de cómo la estés manejando)
        Ronda ronda = rondaRepositorio.obtenerUltimaRondaDePartida(partidaId); // Implementá este método si no existe

        if (ronda == null) {
            throw new IllegalStateException("No hay una ronda activa para esta partida.");
        }

        // 3. Obtener la palabra correcta
        Palabra palabraCorrecta = ronda.getPalabra();

        // 4. Comparar
        boolean esCorrecto = palabraCorrecta.getDescripcion().equalsIgnoreCase(textoIntentado.trim());


        // 5. Armar resultado
        ResultadoIntentoDto resultado = new ResultadoIntentoDto();
        resultado.setCorrecto(esCorrecto);
        resultado.setJugador(nombreJugador);
        resultado.setPalabraCorrecta(palabraCorrecta.getDescripcion());

        return resultado;
    }

    @Override
    public DefinicionDto iniciarNuevaRonda(Long partidaId) {
        // Obtener la partida (usando el repositorio, o el método que tengas para acceder a la partida)
        Partida partida = partidaRepository.buscarPorId(partidaId);
        if (partida == null) {
            throw new IllegalArgumentException("No existe la partida con ID: " + partidaId);
        }

        String idioma = partida.getIdioma();

        // Crear la ronda usando el idioma de la partida
        Ronda ronda = rondaService.crearRonda(partidaId, idioma);

        Palabra palabra = ronda.getPalabra();

        // Obtenemos una definición (por ejemplo la primera)
        String definicionTexto = palabra.getDefiniciones().stream()
                .findFirst()
                .map(Definicion::getDefinicion)
                .orElse("Definición no disponible");



        //verificarRonda - actualizarPuntajeRepo al finalizarRonda//
        //traer lista de jugadores con puntaje 0

        // Armar el DTO para enviar al frontend
        DefinicionDto dto = new DefinicionDto();
        dto.setPalabra(palabra.getDescripcion());
        dto.setDefinicionTexto(definicionTexto);
        dto.setNumeroDeRonda(ronda.getNumeroDeRonda());

        simpMessagingTemplate.convertAndSend("/topic/juego/"+partidaId, dto);
        this.finalizarRondaPorTimer = timerRonda.schedule(
                () -> finalizarRonda(ronda),
                60, TimeUnit.SECONDS
        );


        return dto;
    }

    public void finalizarRonda(Ronda ronda) {
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

}
