package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.EstadoJugador;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ControladorSalaDeEsperaWebSocket {

    @MessageMapping("/salaDeEspera")
    @SendTo("/topic/salaDeEspera")
    public EstadoJugador actualizarEstadoJugador(EstadoJugador estadoJugador,String datosSesion) {
        // Aquí podrías agregar lógica adicional si es necesario
        //Aca pongo la idea de que vengan datos de la sesion y que compares si los datos de la sesion como por ejemplo
        // el id del usuario es igual al id del estadoJugador, si no son iguales quiere decir que el otro jugador
        //intenta cambiar el estado que no es suyo
        String idGuardadoEnSesion = datosSesion;
        if(!idGuardadoEnSesion.equals(estadoJugador.getJugadorId())){
            throw new IllegalArgumentException("No se puede actualizar el estado del otro jugador");
        }
        return estadoJugador; // Retransmite el estado del jugador a todos los clientes
    }

}