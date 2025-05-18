package com.tallerwebi.presentacion;

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

    @Autowired
    public ControladorJuego(RondaServicio rondaServicio) {
        this.rondaServicio = rondaServicio;
    @GetMapping
    public String mostrarVistaJuego(Model model, @RequestParam String jugadorId) {
        // Agregar al jugador si no está
        partida.agregarJugador(jugadorId);

        // Poner la definición y palabra actual (simulada por ahora)
        partida.setPalabraActual("example");
        partida.setDefinicionActual("A sample word for demonstration purposes.");

        model.addAttribute("definicion", partida.getDefinicionActual());
        model.addAttribute("jugadorId", jugadorId);
        model.addAttribute("rondaActual", partida.getRondaActual());
        model.addAttribute("palabra", partida.getPalabraActual());

        return "juego";
    }

    @GetMapping
    public String mostrarJuego(Model model) {
        model.addAttribute("rondaActual", rondaServicio.obtenerNumeroRonda());
        return "juego"; // apunta a juego.html
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
}



