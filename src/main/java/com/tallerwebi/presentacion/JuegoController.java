package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.excepcion.PalabraNoDisponibleException;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.AciertoRepository;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.UsuarioPartidaRepository;
import com.tallerwebi.infraestructura.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/juego")
public class JuegoController {

    // Servicios
    private final RondaService rondaServicio;
    private final PuntajeService puntajeServicio;
    private final PartidaService partidaServicio;
    private final UsuarioService usuarioServicio;
    //private final DefinicionService definicionServicio;
    private final PalabraService palabraServicio;
    private final AciertoService aciertoServicio;

    // Repositorios (para consultas específicas si es necesario)
    private final UsuarioRepository usuarioRepository;
    private final PartidaRepository partidaRepository;
    private final RondaRepository rondaRepository;
    private final PalabraRepository palabraRepository;
    // private final DefinicionRepository definicionRepository;
    private final UsuarioPartidaRepository usuarioPartidaRepository;
    private final AciertoRepository aciertoRepository;

    @Autowired
    public JuegoController(RondaService rondaServicio,
                           PuntajeService puntajeServicio,
                           PartidaService partidaServicio,
                           UsuarioService usuarioServicio,
                           //DefinicionService definicionServicio,
                           PalabraService palabraServicio,
                           AciertoService aciertoServicio,
                           UsuarioRepository usuarioRepository,
                           PartidaRepository partidaRepository,
                           RondaRepository rondaRepository,
                           PalabraRepository palabraRepository,
                           //DefinicionRepository definicionRepository,
                           UsuarioPartidaRepository usuarioPartidaRepository,
                           AciertoRepository aciertoRepository) {
        this.rondaServicio = rondaServicio;
        this.puntajeServicio = puntajeServicio;
        this.partidaServicio = partidaServicio;
        this.usuarioServicio = usuarioServicio;
        //this.definicionServicio = definicionServicio;
        this.palabraServicio = palabraServicio;
        this.aciertoServicio = aciertoServicio;
        this.usuarioRepository = usuarioRepository;
        this.partidaRepository = partidaRepository;
        this.rondaRepository = rondaRepository;
        this.palabraRepository = palabraRepository;
        // this.definicionRepository = definicionRepository;
        this.usuarioPartidaRepository = usuarioPartidaRepository;
        this.aciertoRepository = aciertoRepository;
    }

    @GetMapping
    public ModelAndView mostrarVistaJuego(@RequestParam Long usuarioId,@RequestParam Long partidaId) {

        // El usuario ya está registrado, solo lo obtenemos
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        // La partida ya existe (viene del lobby), solo la obtenemos
        Partida2 partida = partidaRepository.buscarPorId(partidaId);
        if (partida == null) {
            throw new RuntimeException("Partida no encontrada");
        }

        // Verificar que el usuario está en esta partida
        UsuarioPartida usuarioPartida = usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId);
        if (usuarioPartida == null) {
            throw new RuntimeException("El usuario no pertenece a esta partida");
        }

        // Si la partida está "EN_ESPERA", cambiarla a "EN_CURSO" al iniciar
        if (partida.getEstado() == Estado.EN_ESPERA) {
            partida.setEstado(Estado.EN_CURSO);
            partidaRepository.actualizar(partida);
        }

        // Verificar si necesitamos crear la primera ronda
        Ronda rondaActual = rondaRepository.buscarRondaActivaPorPartidaId(partida.getId());
        if (rondaActual == null) {
            // Obtener palabra con sus definiciones usando tu servicio existente
            // Asumo que la partida tiene un idioma configurado, si no usar "Mixto" por defecto
            String idiomaPartida = partida.getIdioma() != null ? partida.getIdioma() : "Mixto";
            HashMap<Palabra, List<Definicion>> palabraConDefiniciones = palabraServicio.traerPalabraYDefinicion(idiomaPartida);

            // Extraer la palabra y una definición aleatoria
            Palabra palabraTexto = palabraConDefiniciones.keySet().iterator().next();
            List<Definicion> definiciones = palabraConDefiniciones.get(palabraTexto);
            Definicion definicionSeleccionada = definiciones.get(new Random().nextInt(definiciones.size()));

            // Crear nueva ronda
            rondaActual = new Ronda();
            rondaActual.setPartida(partida);
            rondaActual.setPalabra(palabraTexto); // Si tienes este campo
            rondaActual.setDefinicion(definicionSeleccionada.getDefinicion()); // Si tienes este campo
            // O si prefieres guardar las entidades completas:
            // rondaActual.setPalabra(palabraServicio.buscarPorTexto(palabraTexto));
            // rondaActual.setDefinicion(definicionSeleccionada);
            rondaActual.setNumeroDeRonda(1); // Primera ronda
            rondaActual.setEstado(Estado.EN_CURSO);

            rondaActual = rondaRepository.guardar(rondaActual);
        }
        // Obtener todos los participantes de la partida con sus puntajes actuales
        List<UsuarioPartida> participantes = usuarioPartidaRepository.buscarPorPartidaId(partida.getId());
        List<Map<String, Object>> jugadoresView = participantes.stream()
                .map(up -> {
                    Map<String, Object> datos = new HashMap<>();
                    datos.put("nombre", up.getUsuario().getNombreUsuario());
                    datos.put("puntaje", up.getPuntaje());
                    datos.put("usuarioId", up.getUsuario().getId());
                    datos.put("esJugadorActual", up.getUsuario().getId().equals(usuarioId));
                    return datos;
                }).collect(Collectors.toList());

        // Preparar la vista del juego
        ModelAndView mov = new ModelAndView("juego");
        mov.addObject("definicion", obtenerDefinicionDeRonda(rondaActual));
        mov.addObject("usuarioId", usuarioId);
        mov.addObject("partidaId", partida.getId());
        mov.addObject("rondaActual", rondaActual.getNumeroDeRonda());
        mov.addObject("palabra", obtenerPalabraDeRonda(rondaActual)); // Solo para debug, no mostrar en la vista real
        mov.addObject("jugadores", jugadoresView);
        mov.addObject("estadoPartida", partida.getEstado());
        mov.addObject("rondaId", rondaActual.getId());
        mov.addObject("maxRondas", partida.getRondasTotales()); // O el límite de rondas que tengas definido
        mov.addObject("nombrePartida", partida.getNombre());

        return mov;
    }

    private Palabra obtenerPalabraDeRonda(Ronda rondaActual) {
        if (rondaActual.getPalabra() != null) {
            return rondaActual.getPalabra();
        }
        throw new PalabraNoDisponibleException();
    }

    private Object obtenerDefinicionDeRonda(Ronda rondaActual) {
        if (rondaActual.getDefinicion() != null) {
            return rondaActual.getDefinicion();
        }
        return "Definición no disponible";
    }
/*
    @PostMapping("/intentar")
    @ResponseBody
    public Map<String, Object> procesarIntentoAjax(@RequestParam String intento,
                                                   @RequestParam String jugadorId,
                                                   @RequestParam int tiempoRestante) {

        Partida partida = partidaServicio.obtenerPartida(jugadorId);
        boolean acierto = intento.equalsIgnoreCase(partida.getPalabraActual());
        Map<String, Object> response = new HashMap<>();

        if (acierto) {
            int puntos = calcularPuntosSegunTiempo(tiempoRestante);
            puntajeServicio.registrarPuntos(jugadorId, puntos);

            if (!partida.isPartidaTerminada()) {
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
        Partida partida = partidaServicio.obtenerPartida(jugadorId);
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
        Partida partida = partidaServicio.obtenerPartida(jugadorId);

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
    }*/
}
