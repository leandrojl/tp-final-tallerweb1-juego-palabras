package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service("servicioSalaDeEspera")
@Transactional
public class SalaDeEsperaServiceImpl implements SalaDeEsperaService {

    SimpMessagingTemplate simpMessagingTemplate;
    private final Set<String> usuariosEnSala = ConcurrentHashMap.newKeySet();
    private UsuarioPartidaRepository usuarioPartida;
    private PartidaRepository partidaRepo;

    public SalaDeEsperaServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Autowired
    public SalaDeEsperaServiceImpl(SimpMessagingTemplate simpMessagingTemplate,
                                   UsuarioPartidaRepository usuarioPartida,PartidaRepository partida) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.usuarioPartida = usuarioPartida;
        this.partidaRepo = partida;
    }



    @Override
    public Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros) {
        Map<Long, Boolean> jugadores = new HashMap<>();
        parametros.forEach((clave, valor) -> {
            if (clave.startsWith("jugador_")) {
                Long jugadorId = Long.parseLong(clave.replace("jugador_", ""));
                Boolean listo = Boolean.parseBoolean(valor);
                jugadores.put(jugadorId, listo);
            }
        });

        return jugadores;
    }

    @Override
    public List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores) {
        List<Long> jugadoresNoListos = new ArrayList<>();

        for (Map.Entry<Long, Boolean> entry : jugadores.entrySet()) {
            Long jugadorId = entry.getKey();
            Boolean listo = entry.getValue();

            if (!listo) {
                jugadoresNoListos.add(jugadorId);
            }
        }

        return jugadoresNoListos;
    }

    @Override
    public void irAlJuego(){
        this.simpMessagingTemplate.convertAndSend("topic/iniciarPartida",new MensajeEnviadoDTO("server","redirect"));
    }

    @Override
    public void mostrarAUnUsuarioLosUsuariosExistentesEnSala(String nombreUsuarioQueAcabaDeUnirseALaSala) {
        usuariosEnSala.add(nombreUsuarioQueAcabaDeUnirseALaSala);
        this.simpMessagingTemplate.convertAndSendToUser(
                nombreUsuarioQueAcabaDeUnirseALaSala,
                "/queue/jugadoresExistentes",
                new ListaUsuariosDTO(new ArrayList<>(usuariosEnSala))
        );
    }

    @Override
    public void redireccionarUsuariosAPartida(MensajeRecibidoDTO mensajeRecibidoDTO) {
        Long idPartida = mensajeRecibidoDTO.getNumber();
        this.partidaRepo.actualizarEstado(idPartida,Estado.EN_CURSO);
        List<Usuario> usuarios = usuarioPartida.obtenerUsuariosDeUnaPartida(idPartida);
            for (Usuario usuario : usuarios) {
                simpMessagingTemplate.convertAndSendToUser(usuario.getNombreUsuario(), "/queue/irAPartida", new MensajeRecibidoDTO(
                        "http://localhost:8080/spring/lobby")); // PROVISIONAL PARA QUE  FUNCIONE
            }
    }

}

