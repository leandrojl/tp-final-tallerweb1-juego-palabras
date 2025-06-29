package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
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

    //EN WEBSOCKETS

    @Override
    public void mostrarAUnUsuarioLosUsuariosExistentesEnSala(String nombreUsuarioQueAcabaDeUnirseALaSala, Long idPartida) {
        usuariosEnSala.add(nombreUsuarioQueAcabaDeUnirseALaSala);
        for (String usuario : usuariosEnSala) {
            this.simpMessagingTemplate.convertAndSendToUser(
                    usuario,
                    "/queue/jugadoresExistentes",
                    new ListaUsuariosDTO(new ArrayList<>(usuariosEnSala))
            );
        }
        this.simpMessagingTemplate.convertAndSend(
                "/topic/cuandoUsuarioSeUneASalaDeEspera/" + idPartida
                ,new MensajeRecibidoDTO(nombreUsuarioQueAcabaDeUnirseALaSala)
        );
    }


    @Override
    public Boolean actualizarElEstadoDeUnUsuario(EstadoJugadorDTO estadoJugadorDTO, String nombreUsuarioDelPrincipal) {
        Long idPartida = estadoJugadorDTO.getIdPartida();
        if(estadoJugadorDTO.getUsername().equals(nombreUsuarioDelPrincipal)){
            this.simpMessagingTemplate.convertAndSend("/topic/salaDeEspera/" + idPartida, estadoJugadorDTO);
            return true;
        }
        return false;
    }

    //PARA SPRINT 4
    @Override
    public Boolean redireccionarUsuariosAPartida(MensajeRecibidoDTO mensajeRecibidoDTO) {
        Long idPartida = mensajeRecibidoDTO.getNumber();

        Partida partida = usuarioPartida.obtenerPartida(idPartida);
        //cambiar el estado de usuarioPartida a EN_CURSO DE Todos los usuarios de la partid

        List<Usuario> usuarios = usuarioPartida.obtenerUsuariosDeUnaPartida(idPartida);
        if(partida.getMinimoJugadores() > usuarios.size()) {
            return false;
        } // PARA SPRINT 4
        this.partidaRepo.actualizarEstado(idPartida,Estado.EN_CURSO);
            for (Usuario usuario : usuarios) {
                simpMessagingTemplate.convertAndSendToUser(usuario.getNombreUsuario(), "/queue/irAPartida",
                        new MensajeRecibidoDTO(
                        "http://localhost:8080/spring/lobby")); // PROVISIONAL PARA QUE  FUNCIONE
            }
            return true;
    }

    @Override
    public MensajeRecibidoDTO abandonarSala(MensajeDto mensaje,String nombreUsuario) {
        Long idUsuario = mensaje.getIdUsuario();
        Long idPartida = mensaje.getIdPartida();
        usuariosEnSala.remove(nombreUsuario);
        this.usuarioPartida.borrarUsuarioPartidaAsociadaAlUsuario(idPartida,idUsuario);
        //List<Usuario> usuarios = this.usuarioPartida.obtenerUsuariosDeUnaPartida(idPartida); PARA DESPUES DEL MERGE
        for (String usuario : usuariosEnSala) {
            this.simpMessagingTemplate.convertAndSendToUser(
                    usuario,
                    "/queue/jugadoresExistentes",
                    new ListaUsuariosDTO(new ArrayList<>(usuariosEnSala))
            );
        }
        return new MensajeRecibidoDTO("http://localhost:8080/spring/lobby");
    }

}

