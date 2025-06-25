package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DefinicionDto;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.RondaDto;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import com.tallerwebi.dominio.JugadorPuntajeDto;

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
    private final UsuarioPartidaService usuarioPartidaService;
    private final UsuarioService usuarioService;

    @Autowired
    public JuegoController(RondaService rondaServicio, PuntajeService puntajeServicio, PartidaService partidaServicio,
                           UsuarioPartidaService usuarioPartidaService,
                           UsuarioService usuarioService) {
        this.rondaServicio = rondaServicio;
        this.puntajeServicio = puntajeServicio;
        this.partidaServicio = partidaServicio;
        this.usuarioPartidaService = usuarioPartidaService;
        this.usuarioService = usuarioService;
    }
    @GetMapping
    public ModelAndView mostrarVistaJuego(HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioID");
        String nombreUsuario = (String) session.getAttribute("usuario");

        if (usuarioId == null || nombreUsuario == null) {
            return new ModelAndView("redirect:/login");
        }

        Long partidaId = (Long) session.getAttribute("partidaID");

        if (partidaId != null) {
            Partida2 partida = partidaServicio.obtenerPartidaPorId(partidaId);

            if (partida == null) {
                // Log para debug
                System.out.println("Partida con id " + partidaId + " no existe.");
                session.removeAttribute("partidaID");
                partidaId = null;
            } else if (partida.getEstado() == Estado.FINALIZADA) {
                System.out.println("Partida con id " + partidaId + " está finalizada.");
                session.removeAttribute("partidaID");
                partidaId = null;
            }
        }

        ModelMap model = new ModelMap();
        model.put("usuarioId", usuarioId);
        model.put("usuario", nombreUsuario);

        if (partidaId == null) {
            // Crear nueva partida
            Partida2 nuevaPartida = generarPartida(nombreUsuario);

            Serializable id = partidaServicio.crearPartida(nuevaPartida);
            nuevaPartida.setId((Long) id);
            session.setAttribute("partidaID", nuevaPartida.getId());

            Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
            usuarioPartidaService.asociarUsuarioConPartida(usuario, nuevaPartida);

            RondaDto dto = partidaServicio.iniciarNuevaRonda(nuevaPartida.getId());

            model.put("partidaId", nuevaPartida.getId());
            model.put("palabra", dto.getPalabra());
            model.put("definicion", dto.getDefinicionTexto());
            model.put("rondaActual", dto.getNumeroDeRonda());

            int puntaje = dto.getJugadores().stream()
                    .filter(j -> j.getNombre().equals(nombreUsuario))
                    .map(JugadorPuntajeDto::getPuntaje)
                    .findFirst()
                    .orElse(0);
            model.put("puntaje", puntaje);

            return new ModelAndView("juego", model);
        }

        // Si ya hay partida válida, cargo info actual
        RondaDto definicion = partidaServicio.obtenerPalabraYDefinicionDeRondaActual(partidaId);

        if (definicion == null) {
            System.out.println("No se pudo obtener la ronda actual para partidaId=" + partidaId);
            session.removeAttribute("partidaID");
            return new ModelAndView("redirect:/juego");
        }

        model.put("partidaId", partidaId);
        model.put("palabra", definicion.getPalabra());
        model.put("definicion", definicion.getDefinicionTexto());
        model.put("rondaActual", definicion.getNumeroDeRonda());

        int puntaje = definicion.getJugadores().stream()
                .filter(j -> j.getNombre().equals(nombreUsuario))
                .map(JugadorPuntajeDto::getPuntaje)
                .findFirst()
                .orElse(0);
        model.put("puntaje", puntaje);

        return new ModelAndView("juego", model);
    }




    @PostMapping("/abandonarPartida")
    @ResponseBody
    public ResponseEntity<String> abandonarPartida(@RequestParam Long usuarioId,
                                                   @RequestParam Long partidaId,
                                                   HttpSession session) {
        UsuarioPartida relacion = usuarioPartidaService.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId);

        if (relacion != null) {
            usuarioPartidaService.marcarComoPerdedor(usuarioId, partidaId);

            // 🔥 Limpiar atributos de sesión relacionados a la partida
            session.removeAttribute("partidaID");

            return ResponseEntity.ok("OK");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No encontrado");
    }


    private static Partida2 generarPartida(String nombreUsuario) {
        Partida2 nuevaPartida = new Partida2();
        nuevaPartida.setNombre("Partida de " + nombreUsuario);
        nuevaPartida.setIdioma(Math.random() < 0.5 ? "Castellano" : "Ingles");
        nuevaPartida.setPermiteComodin(false);
        nuevaPartida.setRondasTotales(5);
        nuevaPartida.setMaximoJugadores(1);
        nuevaPartida.setMinimoJugadores(1);
        nuevaPartida.setEstado(Estado.EN_CURSO);
        return nuevaPartida;
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
