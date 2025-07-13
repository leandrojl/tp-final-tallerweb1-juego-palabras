package com.tallerwebi.dominio.ServicioImplementacion;

import com.tallerwebi.dominio.DTO.EstadoJugadorDTO;
import com.tallerwebi.dominio.DTO.ListaUsuariosDTO;
import com.tallerwebi.dominio.DTO.MensajeDto;
import com.tallerwebi.dominio.DTO.MensajeRecibidoDTO;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.excepcion.CantidadDeUsuariosInsuficientesException;
import com.tallerwebi.dominio.excepcion.CantidadDeUsuariosListosInsuficientesException;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service("servicioSalaDeEspera")
@Transactional
public class SalaDeEsperaServiceImpl implements SalaDeEsperaService {

    SimpMessagingTemplate simpMessagingTemplate;
    private UsuarioPartidaRepository usuarioPartidaRepo;
    private PartidaRepository partidaRepo;
    private UsuarioRepository usuarioRepo;
    private PartidaService partidaService;
    private UsuarioPartidaService usuarioPartidaService;

    public SalaDeEsperaServiceImpl(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Autowired
    public SalaDeEsperaServiceImpl(SimpMessagingTemplate simpMessagingTemplate,
                                   UsuarioPartidaRepository usuarioPartidaRepo,
                                   PartidaRepository partida,
                                   UsuarioRepository usuarioRepository,
                                   PartidaService partidaService,
                                   UsuarioPartidaService usuarioPartidaService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.usuarioPartidaRepo = usuarioPartidaRepo;
        this.partidaRepo = partida;
        this.usuarioRepo = usuarioRepository;
        this.partidaService = partidaService;
        this.usuarioPartidaService = usuarioPartidaService;
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
    public void expulsarJugador(MensajeDto mensajeDto, String nombreUsuarioDelPrincipal) {
        Long idPartida = mensajeDto.getIdPartida();
        Usuario creador = usuarioRepo.obtenerUsuarioPorNombre(nombreUsuarioDelPrincipal);
        Long idCreador = creador.getId();

        // 1. Validamos si es el creador de la partida
        boolean esCreador = partidaService.verificarSiEsElCreadorDePartida(idCreador, idPartida);

        if (!esCreador) {
            // Si no es el creador, lanzamos una excepción
            throw new RuntimeException("Acción no autorizada: solo el creador puede expulsar jugadores.");
        }

        // 2. Obtenemos el NOMBRE del usuario a expulsar
        String nombreUsuarioAExpulsar = mensajeDto.getNombreUsuario();

        // 3. Verificamos que el creador no se expulse a sí mismo
        if (nombreUsuarioDelPrincipal.equals(nombreUsuarioAExpulsar)) {
            throw new RuntimeException("El creador no puede expulsarse a sí mismo.");
        }

        Usuario usuarioAExpulsar = usuarioRepo.obtenerUsuarioPorNombre(nombreUsuarioAExpulsar);
        Long idUsuarioAExpulsar = usuarioAExpulsar.getId();


        this.usuarioRepo.actualizarEstado(idUsuarioAExpulsar,false);

        // 4. Cambiamos el estado del usuario en la tabla UsuarioPartida a CANCELADA
        usuarioPartidaService.cambiarEstado(idUsuarioAExpulsar, idPartida, Estado.CANCELADA);

        // 5. Enviamos un mensaje al tópico específico de la partida
        String destination = "/topic/jugadorExpulsado/" + idPartida;
        MensajeRecibidoDTO mensajeDeExpulsion = new MensajeRecibidoDTO(nombreUsuarioAExpulsar);

        this.simpMessagingTemplate.convertAndSend(destination, mensajeDeExpulsion);

        this.simpMessagingTemplate.convertAndSendToUser(
                nombreUsuarioAExpulsar,
                "/queue/fuisteExpulsado",
                new MensajeRecibidoDTO("/spring/lobby")
        );
    }

    @Override
    public void enviarMensajeDeChat(MensajeDto mensajeDto, String nombreUsuario) {
        // Asignamos el nombre del usuario que envía el mensaje
        mensajeDto.setNombreUsuario(nombreUsuario);

        // Creamos el destino dinámico basado en el idPartida
        String destination = "/topic/chat/" + mensajeDto.getIdPartida();

        // Enviamos el mensaje a todos los suscritos a ese tópico de partida
        simpMessagingTemplate.convertAndSend(destination, mensajeDto);
    }

    @Override
    public void mostrarAUnUsuarioLosUsuariosExistentesEnSala(String nombreUsuarioQueAcabaDeUnirseALaSala, Long idPartida) {
        notificarAUsuariosLosQueEstanEnLaSala(idPartida);
        this.simpMessagingTemplate.convertAndSend(
                "/topic/cuandoUsuarioSeUneASalaDeEspera/" + idPartida
                ,new MensajeRecibidoDTO(nombreUsuarioQueAcabaDeUnirseALaSala)
        );
    }

    private void notificarAUsuariosLosQueEstanEnLaSala(Long idPartida) {
        List<Usuario> usuariosEnSala = usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida);
        List<String> nombres = usuariosEnSala.stream()
                .map(Usuario::getNombreUsuario)
                .collect(Collectors.toList());
        for (Usuario usuario : usuariosEnSala) {
            this.simpMessagingTemplate.convertAndSendToUser(
                    usuario.getNombreUsuario(),
                    "/queue/jugadoresExistentes",
                    new ListaUsuariosDTO(nombres)
            );
        }
    }


    @Override
    public void actualizarElEstadoDeUnUsuario(EstadoJugadorDTO estadoJugadorDTO, String nombreUsuarioDelPrincipal) {
        Long idPartida = estadoJugadorDTO.getIdPartida();
        if(!estadoJugadorDTO.getUsername().equals(nombreUsuarioDelPrincipal)) throw new UsuarioInvalidoException("Error, no se puede alterar el estado de otro jugador");
        Usuario usuario = usuarioPartidaRepo.obtenerUsuarioDeUnaPartidaPorSuNombreUsuario(nombreUsuarioDelPrincipal,idPartida); //TDD
        usuarioRepo.actualizarEstado(usuario.getId(), estadoJugadorDTO.isEstaListo());
        this.simpMessagingTemplate.convertAndSend("/topic/salaDeEspera/" + idPartida, estadoJugadorDTO);
    }

    //PARA SPRINT 4
    @Override
    public void redireccionarUsuariosAPartida(MensajeRecibidoDTO mensajeRecibidoDTO) {
        Long idPartida = mensajeRecibidoDTO.getNumber();
        List<Usuario> usuariosEnSala = validarRedireccionamiento(idPartida);
        this.partidaRepo.actualizarEstado(idPartida,Estado.EN_CURSO);
        this.usuarioPartidaRepo.actualizarEstado(idPartida,Estado.EN_CURSO);
            for (Usuario usuario : usuariosEnSala) {
                simpMessagingTemplate.convertAndSendToUser(usuario.getNombreUsuario(), "/queue/irAPartida",
                        new MensajeRecibidoDTO(
                        "/spring/juego"));
                usuarioRepo.actualizarEstado(usuario.getId(),false); //AGREGAR A LOS TESTS DE SERVICIO COMO MOCK
            }
    }


    @Override
    public MensajeRecibidoDTO abandonarSala(MensajeDto mensaje, String nombreUsuario) {
        Long idPartida = mensaje.getIdPartida();
        Long idUsuario = mensaje.getIdUsuario();

        if (idUsuario == null || idUsuario == 0) {
            Usuario usuario = this.usuarioRepo.obtenerUsuarioPorNombre(nombreUsuario);
            idUsuario = usuario.getId();
        }

        UsuarioPartida usuarioPartida = this.usuarioPartidaRepo.obtenerUsuarioPartida(idUsuario, idPartida);

        if (usuarioPartida.getPartida().getCreadorId().equals(idUsuario)) {
           expulsarATodosLosUsuariosDeLaSala(idPartida);
           return null;
        }

        this.usuarioRepo.actualizarEstado(idUsuario,false);
        this.usuarioPartidaRepo.borrarUsuarioPartidaAsociadaAlUsuario(idPartida, idUsuario);
        notificarAUsuariosLosQueEstanEnLaSala(idPartida);
        return new MensajeRecibidoDTO(
                "/spring/lobby");
    }


    private void expulsarATodosLosUsuariosDeLaSala(Long idPartida) {
        List<Usuario> usuariosEnSala = usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida);
        for (Usuario usuario : usuariosEnSala) {
            this.usuarioRepo.actualizarEstado(usuario.getId(),false);
            this.usuarioPartidaRepo.borrarUsuarioPartidaAsociadaAlUsuario(idPartida,usuario.getId());
            this.simpMessagingTemplate.convertAndSendToUser(
                    usuario.getNombreUsuario(),
                    "/queue/alAbandonarSala",
                    new MensajeRecibidoDTO("/spring/lobby")
            );
        }
    }

    private List<Usuario> validarRedireccionamiento(Long idPartida) {
        Partida partida = usuarioPartidaRepo.obtenerPartida(idPartida);
        List<Usuario> usuariosEnSala = usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida);
        List<Usuario> usuariosListos = usuarioPartidaRepo.obtenerUsuariosListosDeUnaPartida(idPartida);
        if(usuariosEnSala.size() < partida.getMinimoJugadores()) throw new CantidadDeUsuariosInsuficientesException(
                "Cantidad insuficiente de usuarios para iniciar partida");// PARA SPRINT 4
        if(usuariosListos.size() < Math.ceil(usuariosEnSala.size() * 0.50)) throw new CantidadDeUsuariosListosInsuficientesException(
                "No hay suficientes usuarios listos para iniciar la partida"); //PARA SPRINT 4
        return usuariosEnSala;
    }

}

