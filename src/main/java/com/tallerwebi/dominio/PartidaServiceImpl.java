package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.RondaRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PartidaServiceImpl implements PartidaService {
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private final PartidaRepository partidaRepository;
    @Autowired
    private final RondaService rondaService;

    @Autowired
    private UsuarioPartidaRepository usuarioPartidaRepository;

    @Autowired
    private final RondaRepository rondaRepositorio;
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Autowired
    private SessionFactory sessionFactory2;


    private final Map<Long, RondaDto> definicionesPorPartida = new HashMap<>();



    @Autowired
    public PartidaServiceImpl(SimpMessagingTemplate simpMessagingTemplate, PartidaRepository partidaRepository, RondaService rondaService, RondaRepository rondaRepositorio) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.partidaRepository = partidaRepository;
        this.rondaService = rondaService;
        this.rondaRepositorio = rondaRepositorio;
    }



    private final Map<String, Partida> partidas = new HashMap<>();




    @Override
    public Partida2 obtenerPartidaPorId(Long partidaId) {
        return partidaRepository.buscarPorId(partidaId);
    }

    @Override
    public void eliminarPartida(String jugadorId) {
        partidas.remove(jugadorId);
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
        Partida2 partida = partidaRepository.buscarPorId(partidaId);
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
/*
    @Override
    public DefinicionDto iniciarNuevaRonda(Long partidaId) {
        // Obtener la partida (usando el repositorio, o el método que tengas para acceder a la partida)
        Partida2 partida = partidaRepository.buscarPorId(partidaId);

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

        definicionesPorPartida.put(partidaId, dto);

        simpMessagingTemplate.convertAndSend("/topic/juego/"+partidaId, dto);
        return dto;
    }

 */

    @Override
    public RondaDto iniciarNuevaRonda(Long partidaId) {
        Partida2 partida = partidaRepository.buscarPorId(partidaId);
        if (partida == null) {
            throw new IllegalArgumentException("No existe la partida con ID: " + partidaId);
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

        simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId, dto);

        MensajeTipoRanking mensaje = new MensajeTipoRanking("actualizar-puntajes", jugadoresDto);
        simpMessagingTemplate.convertAndSend("/topic/juego/" + partidaId, mensaje);

        return dto;
    }




    @Override
    public Serializable crearPartida(Partida2 nuevaPartida) {
        return partidaRepository.crearPartida(nuevaPartida);
    }


    @Override
    public RondaDto obtenerPalabraYDefinicionDeRondaActual(Long partidaId) {
        return definicionesPorPartida.get(partidaId);
    }

}
