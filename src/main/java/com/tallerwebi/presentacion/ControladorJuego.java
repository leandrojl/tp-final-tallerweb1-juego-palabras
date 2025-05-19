package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import com.tallerwebi.dominio.RondaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/juego")
public class ControladorJuego {

    private final RondaServicio rondaServicio;
    private final HttpSession session;
    private Partida partida = new Partida();  // Instancia global para la partida

    @Autowired
    public ControladorJuego(RondaServicio rondaServicio, HttpSession session) {
        this.rondaServicio = rondaServicio;
        this.session = session;
    }
    @GetMapping
    public String mostrarVistaJuego(Model model, @RequestParam String jugadorId) {

        partida.agregarJugador(jugadorId);


        if (partida.getPalabraActual() == null) {
            HashMap<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
            String palabra = pYD.keySet().iterator().next();
            String definicion = pYD.get(palabra);

            partida.setPalabraActual(palabra);
            partida.setDefinicionActual(definicion);
        }

        model.addAttribute("definicion", partida.getDefinicionActual());
        model.addAttribute("jugadorId", jugadorId);
        model.addAttribute("rondaActual", partida.getRondaActual());
        model.addAttribute("palabra", partida.getPalabraActual()); // para debug, luego ocultar

        return "juego";
    }


    @PostMapping("/fin-ronda")
    @ResponseBody
    public Map<String, Object> finRonda() {
        boolean haySiguiente = rondaServicio.avanzarRonda();

        Map<String, Object> response = new HashMap<>();
        response.put("rondaActual", rondaServicio.obtenerNumeroRonda());
        response.put("partidaTerminada", !haySiguiente);

        if (haySiguiente) {
            HashMap<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
            String palabra = pYD.keySet().iterator().next();
            String definicion = pYD.get(palabra);

            partida.setPalabraActual(palabra);
            partida.setDefinicionActual(definicion);

            response.put("nuevaDefinicion", definicion);
            response.put("nuevaPalabra", palabra);
        }

        return response;
    }


    @PostMapping("/intentar")
    @ResponseBody
    public Map<String, Object> procesarIntentoAjax(@RequestParam String intento,
                                                   @RequestParam String jugadorId) {
        boolean acierto = intento.equalsIgnoreCase(partida.getPalabraActual());

        if (acierto) {
            partida.actualizarPuntos(jugadorId, 1);

            if (!partida.isPartidaTerminada()) {
                partida.avanzarRonda();

                // Nueva palabra y definición para la siguiente ronda
                HashMap<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
                String palabra = pYD.keySet().iterator().next();
                String definicion = pYD.get(palabra);

                partida.setPalabraActual(palabra);
                partida.setDefinicionActual(definicion);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("correcto", acierto);
        response.put("ronda", partida.getRondaActual());
        response.put("puntaje", partida.getPuntaje(jugadorId));
        response.put("partidaTerminada", partida.isPartidaTerminada());

        if (acierto && !partida.isPartidaTerminada()) {
            response.put("nuevaDefinicion", partida.getDefinicionActual());
            // En el front reiniciar temporizador, mostrar nueva definición, etc
        }

        return response;
    }

    @GetMapping("/datos")
    @ResponseBody
    public Map<String, String> getDatosJuego() {
        Map<String, String> datos = new HashMap<>();
        datos.put("palabra", partida.getPalabraActual());
        datos.put("definicion", partida.getDefinicionActual());
        return datos;
    }






}




