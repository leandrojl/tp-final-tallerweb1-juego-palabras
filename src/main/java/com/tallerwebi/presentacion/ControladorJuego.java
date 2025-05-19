package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ControladorJuego {

    private Partida partida = new Partida();
    // Instancia global para la partida

    @Autowired
    public ControladorJuego(Partida partida) {
        this.partida = partida;
    }

    @RequestMapping("/juego")
    public ModelAndView mostrarVistaDeJuego(@RequestParam String idJugador) {
        ModelAndView mov = new ModelAndView("juego");

        partida.agregarJugador(idJugador);

        mov.addObject("idJugador", idJugador);
        mov.addObject("palabraActual", "example");
        mov.addObject("definicionPalabraActual", "A sample word for demonstration purposes.");


        return mov;
    }



//    @GetMapping
//    public String mostrarVistaJuego(Model model, @RequestParam String jugadorId) {
//        // Agregar al jugador si no está
//        partida.agregarJugador(jugadorId);
//
//        // Poner la definición y palabra actual (simulada por ahora)
//        partida.setPalabraActual("example");
//        partida.setDefinicionActual("A sample word for demonstration purposes.");
//
//        model.addAttribute("definicion", partida.getDefinicionActual());
//        model.addAttribute("jugadorId", jugadorId);
//        model.addAttribute("rondaActual", partida.getRondaActual());
//        model.addAttribute("palabra", partida.getPalabraActual());
//
//        return "juego";
//    }

//    @PostMapping("/intentar")
//    @ResponseBody
//    public Map<String, Object> procesarIntentoAjax(@RequestParam String intento,
//                                                   @RequestParam String jugadorId) {
//        System.out.println("INTENTO: " + intento + " - jugador: " + jugadorId);
//
//        boolean acierto = intento.equalsIgnoreCase(partida.getPalabraActual());
//
//        if (acierto) {
//            partida.actualizarPuntos(jugadorId, 1);
//        }
//
//        partida.avanzarRonda();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("correcto", acierto);
//        response.put("ronda", partida.getRondaActual());
//        response.put("puntaje", partida.getPuntaje(jugadorId));
//        response.put("partidaTerminada", partida.isPartidaTerminada());
//
//        return response;
//    }



    @PostMapping("/abandonar")
    public String abandonarPartida(@RequestParam String jugadorId) {
        // Lógica para abandonar (puedes remover al jugador si lo deseas)
        return "redirect:/lobby?jugadorId=" + jugadorId;
    }


    @PostMapping("/intento")
    @ResponseBody
    public Map<String, Object> procesarIntentoConAjax(@RequestParam String intento, @RequestParam String idJugador) {

        Map<String, Object> resultado = new HashMap<>();

        Boolean acierto = intento.equalsIgnoreCase("example");

        resultado.put("correcto", acierto);

        return resultado;
    }
}

