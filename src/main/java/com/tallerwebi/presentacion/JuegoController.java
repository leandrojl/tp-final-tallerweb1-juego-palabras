package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.model.EstadoPartida;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Controller
public class JuegoController {

    private final PartidaService partidaService;

    @Autowired
    public JuegoController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @GetMapping("/juego")
    public ModelAndView mostrarJuego(@RequestParam String jugadorId) {
        ModelAndView mav = new ModelAndView("juego");
        mav.addObject("jugadorId", jugadorId);

        Partida2 partida = partidaService.obtenerPartidaPorJugador(jugadorId);

        if (partida != null) {
            EstadoPartida estado = partidaService.obtenerEstadoPartida(partida.getId());
            mav.addObject("palabra", estado.getPalabraActual());
            mav.addObject("definicion", estado.getDefinicionActual());
            mav.addObject("rondaActual", estado.getRondaActual());
            mav.addObject("jugadores", estado.getJugadores());
        } else {
            mav.addObject("palabra", "prueba");
            mav.addObject("definicion", "Definición de prueba");
            mav.addObject("rondaActual", 1);
        }

        return mav;
    }
}



