package com.tallerwebi.dominio;

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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service
public class PartidaServiceImpl implements PartidaService {
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private final PartidaRepository partidaRepository;
    @Autowired
    private final RondaService rondaService;

    @Autowired
    private final RondaRepository rondaRepositorio;
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Autowired
    private SessionFactory sessionFactory2;



    @Autowired
    public PartidaServiceImpl(SimpMessagingTemplate simpMessagingTemplate, PartidaRepository partidaRepository, RondaService rondaService, RondaRepository rondaRepositorio) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.partidaRepository = partidaRepository;
        this.rondaService = rondaService;
        this.rondaRepositorio = rondaRepositorio;
    }



    private final Map<String, Partida> partidas = new HashMap<>();

    @Override
    public Partida iniciarNuevaPartida(String jugadorId, String nombre) {
        Partida nueva = new Partida();
        nueva.agregarJugador(jugadorId, nombre);
        partidas.put(jugadorId, nueva);
        return nueva;
    }

    @Override
    public Partida obtenerPartida(String jugadorId) {
        return partidas.get(jugadorId);
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

    @Override
    public DefinicionDto iniciarPrimerRonda(Long partidaId) {
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

        // Armar el DTO para enviar al frontend
        DefinicionDto dto = new DefinicionDto();
        dto.setPalabra(palabra.getDescripcion());
        dto.setDefinicion(definicionTexto);
        dto.setNumeroRonda(ronda.getNumeroDeRonda());

        simpMessagingTemplate.convertAndSend("/topic/juego/"+partidaId, dto);
        return null;
    }

    @Override
    public Serializable crearPartida(Partida2 nuevaPartida) {
        return partidaRepository.crearPartida(nuevaPartida);
    }


}
