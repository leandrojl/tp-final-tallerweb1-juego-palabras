package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/juego")
public class ControladorJuego {

    private final RondaServicio rondaServicio;
    private static Partida partida = new Partida();

    @Autowired
    private PuntajeServicio puntajeServicio;

    @Autowired
    public ControladorJuego(RondaServicio rondaServicio) {
        this.rondaServicio = rondaServicio;
    }

    @GetMapping
    public ModelAndView mostrarVistaJuego(@RequestParam String jugadorId) {
        String nombre = "july3p";
        partida.agregarJugador(jugadorId, nombre);
        puntajeServicio.registrarJugador(jugadorId, new Jugador(jugadorId, nombre,"julianomarmaruca@hotmail.com", "pass"));// temporal, desps obtener nombre de la bdd



        if (partida.getPalabraActual() == null) {
            // Cargar palabra y definicion iniciales desde el servicio
            Map<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
            String palabra = pYD.keySet().iterator().next();
            String definicion = pYD.get(palabra);

            puntajeServicio.registrarPuntos(jugadorId, 0);// para inicializar puntos
            partida.avanzarRonda(palabra, definicion); // inicializa la palabra y definición en ronda 1
        }

        List<Map<String, Object>> jugadoresView = partida.getJugadorIds().stream()
                .map(id -> {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre", partida.getNombre(id));
                    datos.put("puntaje", puntajeServicio.obtenerPuntaje(id));
                    return datos;
                }).collect(Collectors.toList());

        ModelAndView mov = new ModelAndView("juego");

        mov.addObject("definicion", partida.getDefinicionActual());
        mov.addObject("jugadorId", jugadorId);
        mov.addObject("rondaActual", partida.getRondaActual());
        mov.addObject("palabra", partida.getPalabraActual());
        mov.addObject("jugadores", jugadoresView);;


        return mov;
    }

    @PostMapping("/intentar")
    @ResponseBody
    public Map<String, Object> procesarIntentoAjax(@RequestParam String intento,
                                                   @RequestParam String jugadorId,
                                                   @RequestParam  int tiempoRestante) {

        boolean acierto = intento.equalsIgnoreCase(partida.getPalabraActual());
        Map<String, Object> response = new HashMap<>();

        if (acierto) {
            int puntos = calcularPuntosSegunTiempo(tiempoRestante);
            puntajeServicio.registrarPuntos(jugadorId, puntos);

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
        response.put("puntaje", puntajeServicio.obtenerPuntaje(jugadorId));

        return response;
    }

    private int calcularPuntosSegunTiempo(int tiempoRestante) {
        if (tiempoRestante > 45) return 100;
        else if (tiempoRestante > 30) return 75;
        else if (tiempoRestante > 15) return 50;
        else if (tiempoRestante > 0) return 25;
        else return 0;
    }

    @PostMapping("/fin-ronda")
    @ResponseBody
    public Map<String, Object> finRonda(@RequestParam String jugadorId) {
        Map<String, Object> response = new HashMap<>();

        if (partida.isPartidaTerminada()) {
            response.put("partidaTerminada", true);
            response.put("rondaActual", partida.getRondaActual());
            response.put("jugadorId", jugadorId);
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

    @GetMapping("/final")
    public String mostrarVistaFinal(@RequestParam String jugadorId, Model model) {
        Map<Jugador, Integer> puntajes = puntajeServicio.obtenerTodosLosPuntajes();

        List<Map.Entry<Jugador, Integer>> ranking = puntajes.entrySet()
                .stream()
                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        String ganador = ranking.get(0).getKey().getNombre();

        String jugadorActual = partida.getNombre(jugadorId);
        if (jugadorActual == null) {
            jugadorActual = "Jugador_" + jugadorId;
        }

        model.addAttribute("ranking", ranking);
        model.addAttribute("ganador", ganador);
        model.addAttribute("jugadorActual", jugadorActual);

        return "vistaFinalJuego";
    }




}
