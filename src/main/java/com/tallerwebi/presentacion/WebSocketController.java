package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.model.MensajeEnviadoDTO;
import com.tallerwebi.dominio.model.MensajeRecibidoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import com.tallerwebi.dominio.model.EstadoJugadorDTO;

import java.security.Principal;

@Controller
public class WebSocketController {


    private PartidaService partidaService;
    private SalaDeEsperaService salaDeEsperaService;

    @Autowired
    public WebSocketController(PartidaService partidaService, SalaDeEsperaService salaDeEsperaService) {
        this.partidaService = partidaService;
        this.salaDeEsperaService = salaDeEsperaService;
    }


    @MessageMapping("/salaDeEspera")
    @SendTo("/topic/salaDeEspera")
    public EstadoJugadorDTO actualizarEstadoJugador(EstadoJugadorDTO estadoJugadorDTO, Principal principal) {
        //Si un usuario intenta cambiar el estado que no es suyo se valida
        String nombreUsuario = estadoJugadorDTO.getUsername();
            if(!nombreUsuario.equals(principal.getName())) {
                throw new UsuarioInvalidoException("Error, no se puede alterar el estado de otro jugador");
            }
        return estadoJugadorDTO;
    }

    @MessageExceptionHandler(UsuarioInvalidoException.class)
    @SendToUser("/queue/mensajeAlIntentarCambiarEstadoDeOtroJugador")
    public MensajeRecibidoDTO handleUsuarioInvalidoException(UsuarioInvalidoException ex) {
        return new MensajeRecibidoDTO(ex.getMessage());
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MensajeEnviadoDTO getMessages(MensajeRecibidoDTO mensajeRecibidoDTO, Principal principal) {
        String nombreUsuario;
        if (principal != null) {
            nombreUsuario = principal.getName();
        } else {
            nombreUsuario = "Anónimo";
        }
        return new MensajeEnviadoDTO(nombreUsuario, mensajeRecibidoDTO.getMessage());
    }
    @MessageMapping("/usuarioSeUneASalaDeEspera")
    @SendTo("/topic/cuandoUsuarioSeUneASalaDeEspera")
    public MensajeRecibidoDTO usuarioSeUneASala(MensajeRecibidoDTO mensajeRecibidoDTO, Principal principal){
        String nombreUsuario = principal.getName();
        this.salaDeEsperaService.mostrarAUnUsuarioLosUsuariosExistentesEnSala(nombreUsuario);
        return new MensajeRecibidoDTO(nombreUsuario);
    }

    public void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        this.partidaService.enviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);
    }

    public void irAlJuego() {
        this.salaDeEsperaService.irAlJuego();
    }

}
