package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DefinicionDto;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.RondaDto;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.PuntajeService;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida2;

import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/juego")
public class JuegoController {

    private final RondaService rondaServicio;
    private final PuntajeService puntajeServicio;
    private final PartidaService partidaServicio;

    @Autowired
    public JuegoController(RondaService rondaServicio, PuntajeService puntajeServicio, PartidaService partidaServicio) {
        this.rondaServicio = rondaServicio;
        this.puntajeServicio = puntajeServicio;
        this.partidaServicio = partidaServicio;
    }
    @GetMapping
    public ModelAndView mostrarVistaJuego(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioID");
        String nombreUsuario = (String) session.getAttribute("usuario");

        if (usuarioId == null || nombreUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        Long partidaId = (Long) session.getAttribute("partidaID");

        // Si no hay partida en sesión, la creo ahora
        if (partidaId == null) {
            Partida2 nuevaPartida = new Partida2();
            nuevaPartida.setNombre("Partida de " + nombreUsuario);

            // Ejemplo idioma aleatorio
            String idioma = Math.random() < 0.5 ? "Castellano" : "Ingles";
            nuevaPartida.setIdioma(idioma);
            nuevaPartida.setPermiteComodin(false);
            nuevaPartida.setRondasTotales(5);
            nuevaPartida.setMaximoJugadores(1);
            nuevaPartida.setMinimoJugadores(1);
            nuevaPartida.setEstado(Estado.EN_CURSO);

            Serializable id = partidaServicio.crearPartida(nuevaPartida);
            nuevaPartida.setId((Long) id);

            // Guardar id en sesión para próximas peticiones
            session.setAttribute("partidaID", nuevaPartida.getId());

            // Crear primera ronda y obtener datos para la vista
            RondaDto dto = partidaServicio.iniciarNuevaRonda(nuevaPartida.getId());

            ModelMap model = new ModelMap();
            model.put("usuarioId", usuarioId);
            model.put("partidaId", nuevaPartida.getId());
            model.put("palabra", dto.getPalabra());
            model.put("definicion", dto.getDefinicionTexto());
            model.put("rondaActual", dto.getNumeroDeRonda());

            return new ModelAndView("juego", model);
        }

        // Si ya existe partida en sesión, solo busco datos para mostrar
        RondaDto definicion = partidaServicio.obtenerPalabraYDefinicionDeRondaActual(partidaId);

        ModelMap model = new ModelMap();
        model.put("usuarioId", usuarioId);
        model.put("partidaId", partidaId);
        model.put("palabra", definicion.getPalabra());
        model.put("definicion", definicion.getDefinicionTexto());
        model.put("rondaActual", definicion.getNumeroDeRonda());

        return new ModelAndView("juego", model);
    }


    /*
    @GetMapping
    public ModelAndView mostrarVistaJuego(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioID");
        String nombreUsuario = (String) session.getAttribute("usuario");

        if (usuarioId == null || nombreUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        // 1. Crear la nueva partida (configurá los datos que quieras, acá ejemplo básico)
        Partida2 nuevaPartida = new Partida2();
        nuevaPartida.setNombre("Partida de " + nombreUsuario);

        String idioma = Math.random() < 0.5 ? "Castellano" : "Ingles";

        nuevaPartida.setIdioma(idioma); // o lo que corresponda
        nuevaPartida.setPermiteComodin(false);
        nuevaPartida.setRondasTotales(5);
        nuevaPartida.setMaximoJugadores(1);
        nuevaPartida.setMinimoJugadores(1);
        nuevaPartida.setEstado(Estado.EN_CURSO);

        // 2. Guardar la partida en la BD
        Serializable idPartida = partidaServicio.crearPartida(nuevaPartida);
        nuevaPartida.setId((Long) idPartida);

        // 3. Crear la primera ronda
        DefinicionDto dto = partidaServicio.iniciarNuevaRonda(nuevaPartida.getId());

        // 4. Guardar la partida y el usuario en sesión para futuras consultas
        session.setAttribute("partidaID", nuevaPartida.getId());
        session.setAttribute("usuarioID", usuarioId);
        session.setAttribute("nombreUsuario", nombreUsuario);

        // 5. Preparar modelo para la vista
        ModelMap model = new ModelMap();
        model.put("usuarioId", usuarioId);
        model.put("rondaActual", dto.getNumeroDeRonda());
        model.put("palabra", dto.getPalabra());
        model.put("definicion", dto.getDefinicionTexto());
        // Si querés, agregá jugadores y puntajes acá (en modo solo hay uno)

        return new ModelAndView("juego", model);
    }

     */


//    @PostMapping("/intentar")
//    @ResponseBody
//    public Map<String, Object> procesarIntentoAjax(@RequestParam String intento,
//                                                   @RequestParam String jugadorId,
//                                                   @RequestParam int tiempoRestante) {
//
//        Partida partida = partidaServicio.obtenerPartida(jugadorId);
//        boolean acierto = intento.equalsIgnoreCase(partida.getPalabraActual());
//        Map<String, Object> response = new HashMap<>();
//
//        if (acierto) {
//            int puntos = calcularPuntosSegunTiempo(tiempoRestante);
//            puntajeServicio.registrarPuntos(jugadorId, puntos);
//
//            if (!partida.isPartidaTerminada()) {
//                Map<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
//                String palabra = pYD.keySet().iterator().next();
//                String definicion = pYD.get(palabra);
//
//                boolean haySiguiente = partida.avanzarRonda(palabra, definicion);
//
//                response.put("nuevaDefinicion", definicion);
//                response.put("nuevaPalabra", palabra);
//                response.put("partidaTerminada", !haySiguiente);
//            } else {
//                response.put("partidaTerminada", true);
//            }
//        } else {
//            response.put("partidaTerminada", partida.isPartidaTerminada());
//        }
//
//        response.put("correcto", acierto);
//        response.put("ronda", partida.getRondaActual());
//        response.put("puntaje", puntajeServicio.obtenerPuntaje(jugadorId));
//
//        return response;
//    }
//
//    private int calcularPuntosSegunTiempo(int tiempoRestante) {
//        if (tiempoRestante > 45) return 100;
//        else if (tiempoRestante > 30) return 75;
//        else if (tiempoRestante > 15) return 50;
//        else if (tiempoRestante > 0) return 25;
//        else return 0;
//    }
//
//    @PostMapping("/fin-ronda")
//    @ResponseBody
//    public Map<String, Object> finRonda(@RequestParam String jugadorId) {
//        Partida partida = partidaServicio.obtenerPartida(jugadorId);
//        Map<String, Object> response = new HashMap<>();
//
//        if (partida.isPartidaTerminada()) {
//            response.put("partidaTerminada", true);
//            response.put("rondaActual", partida.getRondaActual());
//            response.put("jugadorId", jugadorId);
//            return response;
//        }
//
//        Map<String, String> pYD = rondaServicio.traerPalabraYDefinicion();
//        String palabra = pYD.keySet().iterator().next();
//        String definicion = pYD.get(palabra);
//
//        boolean haySiguiente = partida.avanzarRonda(palabra, definicion);
//
//        response.put("partidaTerminada", !haySiguiente);
//        response.put("rondaActual", partida.getRondaActual());
//        if (haySiguiente) {
//            response.put("nuevaPalabra", palabra);
//            response.put("nuevaDefinicion", definicion);
//        }
//
//        return response;
//    }
//
//    @GetMapping("/final")
//    public String mostrarVistaFinal(@RequestParam String jugadorId, Model model) {
//        Partida partida = partidaServicio.obtenerPartida(jugadorId);
//
//        Map<Jugador, Integer> puntajes = puntajeServicio.obtenerTodosLosPuntajes();
//        List<Map.Entry<Jugador, Integer>> ranking = puntajes.entrySet()
//                .stream()
//                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
//                .collect(Collectors.toList());
//
//        String ganador = ranking.get(0).getKey().getNombre();
//        String jugadorActual = partida.getNombre(jugadorId);
//
//        if (jugadorActual == null) {
//            jugadorActual = "Jugador_" + jugadorId;
//        }
//
//        model.addAttribute("ranking", ranking);
//        model.addAttribute("ganador", ganador);
//        model.addAttribute("jugadorActual", jugadorActual);
//
//        return "vistaFinalJuego";
//    }
}
