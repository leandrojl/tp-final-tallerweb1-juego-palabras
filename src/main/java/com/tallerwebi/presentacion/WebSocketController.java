package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.model.MensajeEnviado;
import com.tallerwebi.dominio.model.MensajeRecibido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import com.tallerwebi.dominio.model.EstadoJugador;

import java.security.Principal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    public EstadoJugador actualizarEstadoJugador(EstadoJugador estadoJugador,Principal principal) {
        //Si un usuario intenta cambiar el estado que no es suyo se valida
        String nombreUsuario = estadoJugador.getUsername();
            if(!nombreUsuario.equals(principal.getName())) {
                throw new UsuarioInvalidoException("Error, no se puede alterar el estado de otro jugador");
            }
        return estadoJugador;
    }

    @MessageExceptionHandler(UsuarioInvalidoException.class)
    @SendToUser("/queue/errors")
    public MensajeRecibido handleUsuarioInvalidoException(UsuarioInvalidoException ex) {
        return new MensajeRecibido(ex.getMessage());
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MensajeEnviado getMessages(MensajeRecibido mensajeRecibido, Principal principal) {
        String nombreUsuario;
        if (principal != null) {
            nombreUsuario = principal.getName();
        } else {
            nombreUsuario = "Anónimo";
        }
        return new MensajeEnviado(nombreUsuario,mensajeRecibido.getMessage());
    }
    @MessageMapping("/usuarioSeUneASalaDeEspera")
    public void usuarioSeUneASala(MensajeRecibido mensajeRecibido,Principal principal){
        String nombreUsuario = principal.getName();
        this.salaDeEsperaService.notificarQueSeUneUnNuevoUsuarioALaSala(nombreUsuario);
    }

    public void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        this.partidaService.enviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);
    }

    public void irAlJuego() {
        this.salaDeEsperaService.irAlJuego();
    }

    public void notificarQueSeUneUnNuevoUsuarioALaSala(String nombreUsuarioQueAcabaDeUnirseALaSala) {
        this.salaDeEsperaService.notificarQueSeUneUnNuevoUsuarioALaSala(nombreUsuarioQueAcabaDeUnirseALaSala);
    }
}
