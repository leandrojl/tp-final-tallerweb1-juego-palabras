package com.tallerwebi.presentacion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.MensajeEnviado;
import com.tallerwebi.dominio.MensajeRecibido;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/salaDeEspera")
    @SendTo("/topic/salaDeEspera")
    public EstadoJugador actualizarEstadoJugador(EstadoJugador estadoJugador) { //agregar datosSesion cuando esten
        // Aquí podrías agregar lógica adicional si es necesario
        //Aca pongo la idea de que vengan datos de la sesion y que compares si los datos de la sesion como por ejemplo
        // el id del usuario es igual al id del estadoJugador, si no son iguales quiere decir que el otro jugador
        //intenta cambiar el estado que no es suyo
        /*String idGuardadoEnSesion = datosSesion;
        if(!idGuardadoEnSesion.equals(estadoJugador.getJugadorId())){
            throw new IllegalArgumentException("No se puede actualizar el estado del otro jugador");
        }*/
        return estadoJugador; // Retransmite el estado del jugador a todos los clientes
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MensajeEnviado getMessages(MensajeRecibido mensajeRecibido) {
        return new MensajeEnviado(mensajeRecibido.getMessage());
    }



    public static class EstadoJugador {
        private String jugadorId;
        private boolean estaListo;

        // Getters y setters
        public String getJugadorId() {
            return jugadorId;
        }

        public void setJugadorId(String jugadorId) {
            this.jugadorId = jugadorId;
        }

        public boolean isEstaListo() {
            return estaListo;
        }

        public void setEstaListo(boolean estaListo) {
            this.estaListo = estaListo;
        }
    }
}