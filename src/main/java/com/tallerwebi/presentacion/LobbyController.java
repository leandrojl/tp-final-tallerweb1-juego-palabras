package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.CrearPartidaDTO;
import com.tallerwebi.dominio.Enum.Estado;

import com.tallerwebi.dominio.excepcion.PartidaAleatoriaNoDisponibleException;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Controller
public class LobbyController {

    private LobbyService lobbyService;
    private PartidaService partidaService;
    private UsuarioService usuarioService;
    private UsuarioPartidaService usuarioPartidaService;

    @Autowired
    public LobbyController(PartidaService partidaService,
                           LobbyService lobbyService,
                           UsuarioService usuarioService,
                           UsuarioPartidaService usuarioPartidaService) {
        this.partidaService = partidaService;
        this.lobbyService = lobbyService;
        this.usuarioService = usuarioService;
        this.usuarioPartidaService = usuarioPartidaService;
    }


    @GetMapping("/cerrar-sesion")
    public ModelAndView cerrarSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return new ModelAndView("redirect:/login");
    }


    @GetMapping("/buscar")
    @ResponseBody
    public List<Partida> buscar(@RequestParam("nombre") String nombre) {
        return lobbyService.buscarPartidasPorNombre(nombre);
    }

    @GetMapping("/cerrarSalaDeEspera")
    public String cerrarSalaDeEspera(HttpSession session) {
        Long idPartida = (Long) session.getAttribute("idPartida");
        if (idPartida == null) {
            // No hay partida activa en la sesión
            return "redirect:/lobby";
        }
        Long idUsuario = (Long) session.getAttribute("usuarioId");
        if (idUsuario == null) {
            // El usuario no está autenticado o la sesión expiró
            return "redirect:/login";
        }

        if(partidaService.verificarSiEsElCreadorDePartida(idUsuario, idPartida)){

            partidaService.cancelarPartidaDeUsuario(idUsuario, idPartida);
            usuarioPartidaService.cancelarPartidaDeUsuario(idUsuario, idPartida);
            session.removeAttribute("idPartida");
        } else {

            usuarioPartidaService.cancelarPartidaDeUsuario(idUsuario, idPartida);
            session.removeAttribute("idPartida");
        }

        return "redirect:/lobby";
    }

    @GetMapping("/crear-sala")
    public String mostrarFormularioCrearSala(){
        return "crear-sala";
    }

    @PostMapping("/crear-sala")
    public String crearSala(@ModelAttribute CrearPartidaDTO partidaDTO, HttpSession session) {
        Long idUsuario = (Long) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            return "redirect:/login"; // O manejar el error como prefieras
        }

        Serializable idPartida = partidaService.crearPartida(partidaDTO, idUsuario);
        session.setAttribute("idPartida", idPartida);

        return "redirect:/sala-de-espera";
    }

//    @PostMapping("/crear-sala")
//    public String crearSala(
//            @RequestParam String nombre,
//            @RequestParam String idioma,
//            @RequestParam(required = false) boolean permiteComodin,
//            @RequestParam int rondasTotales,
//            @RequestParam int maximoJugadores,
//            @RequestParam int minimoJugadores,
//            HttpSession session
//    ) {
//
//        Long idUsuario = (Long) session.getAttribute("idUsuario");
//
//        Long creadorDePartida = idUsuario;
//
//        Partida nuevaPartida = new Partida(
//                nombre,
//                idioma,
//                permiteComodin,
//                rondasTotales,
//                maximoJugadores,
//                minimoJugadores,
//                Estado.EN_ESPERA,
//                creadorDePartida);
//
//        Serializable idPartida = partidaService.crearPartida(nuevaPartida);
//
//        session.setAttribute("idPartida", idPartida);
//
//        System.out.println("ID de la partida creada: " + idPartida);
//        System.out.println("ID del usuario: " + idUsuario);
//
//
//        return "redirect:/sala-de-espera"; //peticion http a @RequestMapping("/sala-de-espera") que redirige a la sala de espera
//    }

    @RequestMapping("/partidaAleatoria")
    public String partidaAleatoria(HttpSession session, RedirectAttributes redirectAttributes) {
        try{
            Long idPartida = lobbyService.obtenerUnaPartidaAleatoria();
            session.setAttribute("idPartida", idPartida);
            return "redirect:/unirseAPartidaAleatoria";
        }catch(PartidaAleatoriaNoDisponibleException ex){
            redirectAttributes.addFlashAttribute("partidaAleatoriaNoDisponible", ex.getMessage());
        }

        return "redirect:/lobby";
    }

    @RequestMapping("/lobby")
    public ModelAndView Lobby(HttpSession session, Model model) {
        prepararDatosDeUsuario(session, model);
        prepararPartidasEnEspera(model);
        return new ModelAndView("lobby");
    }

    private void prepararDatosDeUsuario(HttpSession session, Model model) {
        Long idUsuario = (Long) session.getAttribute("idUsuario");
        if (idUsuario != null) {
            Usuario usuario = usuarioService.buscarPorId(idUsuario);
            if (usuario != null) {
                model.addAttribute("usuarioNombre", usuario.getNombreUsuario());
                model.addAttribute("monedas", usuario.getMoneda());
            }
        }
    }

    private void prepararPartidasEnEspera(Model model) {
        List<Partida> partidas = lobbyService.obtenerPartidasEnEspera();
        if (partidas.isEmpty()) {
            model.addAttribute("mensaje", "No hay partidas disponibles en curso.");
        } else {
            model.addAttribute("partidas", partidas);
        }
    }

//    @RequestMapping("/lobby")
//    public ModelAndView Lobby(HttpSession session, Model model) {
//        Long idUsuario = (Long) session.getAttribute("idUsuario");
//        if (idUsuario != null) {
//            Usuario usuario = usuarioService.buscarPorId(idUsuario); // Asegúrate de tener este servicio inyectado
//            if (usuario != null) {
//                model.addAttribute("usuarioNombre", usuario.getNombreUsuario());
//
//            }
//        }
//        // obtengo las partidas en espera
//        List<Partida> partidas = lobbyService.obtenerPartidasEnEspera();
//        if (partidas.isEmpty()) {
//            model.addAttribute("mensaje", "No hay partidas disponibles en curso.");
//        } else {
//            model.addAttribute("partidas", partidas);
//        }
//        return new ModelAndView("lobby");
//    }


    @RequestMapping("/Ranking")
    public ModelAndView Ranking() {
        return new ModelAndView("ranking-global");    }


    @RequestMapping("/Partida")
    public ModelAndView Partida() {
        return new ModelAndView("partida");
    }

    @RequestMapping("/Recompensas")
    public ModelAndView Recompensa() {
        return new ModelAndView("recompensas");
    }

}
