package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.RondaServicio;
import com.tallerwebi.infraestructura.PartidaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/juego")
public class ControladorJuego {

    private final RondaServicio rondaServicio;
    private final PartidaServicio partida;

    @Autowired
    public ControladorJuego(RondaServicio rondaServicio, PartidaServicio partida) {
        this.rondaServicio = rondaServicio;
        this.partida = partida;
    }

    @GetMapping
    public ModelAndView mostrarVistaJuego(@RequestParam String jugadorId) {
        // Agregar al jugador si no está
        partida.agregarJugador(jugadorId);

        if (partida.getPalabraActual() == null) {
            // Cargar palabra y definicion iniciales desde el servicio
            Map<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
            String palabra = pYD.keySet().iterator().next();
            String definicion = pYD.get(palabra);

            partida.actualizarPuntos(jugadorId, 0); // Opcional para inicializar puntos
            partida.avanzarRonda(palabra, definicion); // inicializa la palabra y definición en ronda 1
        }

        ModelAndView mov = new ModelAndView("juego");

        mov.addObject("definicion", partida.getDefinicionActual());
        mov.addObject("jugadorId", jugadorId);
        mov.addObject("rondaActual", partida.getRondaActual());
        mov.addObject("palabra", partida.getPalabraActual());

        return mov;
    }

    @PostMapping("/intentar")
    @ResponseBody
    public Map<String, Object> procesarIntentoAjax(@RequestParam String intento,
                                                   @RequestParam String jugadorId) {

        boolean acierto = intento.equalsIgnoreCase(partida.getPalabraActual());
        Map<String, Object> response = new HashMap<>();

        if (acierto) {
            partida.actualizarPuntos(jugadorId, 1);

            if (!partida.isPartidaTerminada()) {
                // Obtener nueva palabra y definición para la siguiente ronda
                Map<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
                String palabra = pYD.keySet().iterator().next();
                String definicion = pYD.get(palabra);

                boolean haySiguiente = partida.avanzarRonda(palabra, definicion);

                response.put("nuevaDefinicion", definicion);
                response.put("nuevaPalabra", palabra);
                response.put("partidaTerminada", !haySiguiente);
            } else {
                response.put("partidaTerminada", true);
            }
        } else {
            response.put("partidaTerminada", partida.isPartidaTerminada());
        }

        response.put("correcto", acierto);
        response.put("ronda", partida.getRondaActual());
        response.put("puntaje", partida.getPuntaje(jugadorId));

        return response;
    }

    @PostMapping("/fin-ronda")
    @ResponseBody
    public Map<String, Object> finRonda() {
        Map<String, Object> response = new HashMap<>();

        if (partida.isPartidaTerminada()) {
            response.put("partidaTerminada", true);
            response.put("rondaActual", partida.getRondaActual());
            return response;
        }

        Map<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
        String palabra = pYD.keySet().iterator().next();
        String definicion = pYD.get(palabra);

        boolean haySiguiente = partida.avanzarRonda(palabra, definicion);

        response.put("partidaTerminada", !haySiguiente);
        response.put("rondaActual", partida.getRondaActual());
        if (haySiguiente) {
            response.put("nuevaPalabra", palabra);
            response.put("nuevaDefinicion", definicion);
        }

        return response;
    }
}
