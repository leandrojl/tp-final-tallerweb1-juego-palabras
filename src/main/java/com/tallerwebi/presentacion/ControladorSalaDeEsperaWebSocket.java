package com.tallerwebi.presentacion;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ControladorSalaDeEsperaWebSocket {

    @MessageMapping("/salaDeEspera")
    @SendTo("/topic/salaDeEspera")
    public EstadoJugador actualizarEstadoJugador(EstadoJugador estadoJugador) {
        // Aquí podrías agregar lógica adicional si es necesario
        return estadoJugador; // Retransmite el estado del jugador a todos los clientes
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