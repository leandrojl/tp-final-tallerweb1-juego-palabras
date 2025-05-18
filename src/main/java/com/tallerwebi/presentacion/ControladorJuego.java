package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RondaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/juego")
public class ControladorJuego {

    private final RondaServicio rondaServicio;
    private Partida partida = new Partida();  // Instancia global para la partida

    @Autowired
    public ControladorJuego(RondaServicio rondaServicio) {
        this.rondaServicio = rondaServicio;
    }
    @GetMapping
    public String mostrarVistaJuego(Model model, @RequestParam String jugadorId) {
        // Agregar al jugador si no está
        partida.agregarJugador(jugadorId);

        // Poner la definición y palabra actual (simulada por ahora)
        partida.setPalabraActual("example");
        partida.setDefinicionActual("A sample word for demonstration purposes.");

        model.addAttribute("definicion", partida.getDefinicionActual());
        model.addAttribute("jugadorId", jugadorId);
        model.addAttribute("rondaActual", rondaServicio.obtenerNumeroRonda());
        model.addAttribute("palabra", partida.getPalabraActual());
        return "juego";
    }



    @PostMapping("/fin-ronda")
    @ResponseBody
    public Map<String, Object> finRonda() {
        boolean haySiguiente = rondaServicio.avanzarRonda();

        Map<String, Object> response = new HashMap<>();
        response.put("rondaActual", rondaServicio.obtenerNumeroRonda());
        response.put("partidaTerminada", !haySiguiente);

        return response;
    }


    @PostMapping("/intentar")
    @ResponseBody
    public Map<String, Object> procesarIntentoAjax(@RequestParam String intento,
                                                   @RequestParam String jugadorId) {
        System.out.println("INTENTO: " + intento + " - jugador: " + jugadorId);

        boolean acierto = intento.equalsIgnoreCase(partida.getPalabraActual());

        if (acierto) {
            partida.actualizarPuntos(jugadorId, 1);
        }

        partida.avanzarRonda();

        Map<String, Object> response = new HashMap<>();
        response.put("correcto", acierto);
        response.put("ronda", partida.getRondaActual());
        response.put("puntaje", partida.getPuntaje(jugadorId));
        response.put("partidaTerminada", partida.isPartidaTerminada());

        return response;
    }
}




