package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Enum.Estado;

import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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


    @GetMapping("/buscar")
    @ResponseBody
    public List<Partida> buscar(@RequestParam("nombre") String nombre) {
        return lobbyService.buscarPartidasPorNombre(nombre);
    }


    @GetMapping("/crear-sala")
    public String mostrarFormularioCrearSala() {
        return "crear-sala";
    }

    @PostMapping("/crear-sala")
    public String crearSala(
            @RequestParam String nombre,
            @RequestParam String idioma,
            @RequestParam(required = false) boolean permiteComodin,
            @RequestParam int rondasTotales,
            @RequestParam int maximoJugadores,
            @RequestParam int minimoJugadores,
            HttpSession session
    ) {

        Long idUsuario = (Long) session.getAttribute("usuarioId");

        Partida nuevaPartida = new Partida(
                nombre,
                idioma,
                permiteComodin,
                rondasTotales,
                maximoJugadores,
                minimoJugadores,
                Estado.EN_ESPERA);

        Serializable idPartida = partidaService.crearPartida(nuevaPartida);

        session.setAttribute("idPartida", idPartida);

        System.out.println("SOY EL ID PARTIDA PA"+idPartida);

        int puntaje = 0;
        boolean gano = false;
        Estado estado = Estado.EN_ESPERA;

        usuarioPartidaService.agregarUsuarioAPartida(
                idUsuario,
                (Long) idPartida,
                puntaje,
                gano,
                estado);

        System.out.println("ID de la partida creada: " + idPartida);
        System.out.println("ID del usuario: " + idUsuario);


        return "redirect:/sala-de-espera"; //peticion http a @RequestMapping("/sala-de-espera") que redirige a la sala de espera
    }


    @RequestMapping("/lobby")
    public ModelAndView Lobby(HttpSession session, Model model) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        if (usuarioId != null) {
            Usuario usuario = usuarioService.buscarPorId(usuarioId); // Asegúrate de tener este servicio inyectado
            if (usuario != null) {
                model.addAttribute("usuarioNombre", usuario.getNombreUsuario());

            }
        }

        // obtengo las partidas en espera
        List<Partida> partidas = lobbyService.obtenerPartidasEnEspera();
        if (partidas.isEmpty()) {
            model.addAttribute("mensaje", "No hay partidas disponibles en curso.");
        } else {
            model.addAttribute("partidas", partidas);
        }

        return new ModelAndView("lobby");
    }


    @RequestMapping("/Ranking")
    public ModelAndView Ranking() {
        return new ModelAndView("ranking");    }


    @RequestMapping("/Perfil")
    public ModelAndView Perfil() {
        return new ModelAndView("perfil");
    }

    @RequestMapping("/Partida")
    public ModelAndView Partida() {
        return new ModelAndView("partida");
    }

    @RequestMapping("/Recompensas")
    public ModelAndView Recompensa() {
        return new ModelAndView("recompensas");
    }

    @RequestMapping("/irASalaDeEspera")
    public ModelAndView irASalaDeEspera(HttpSession session) {
        ModelMap modelMap = new ModelMap();
        String nombreUsuario = (String) session.getAttribute("usuario");
        modelMap.addAttribute("usuario",nombreUsuario);
        return new ModelAndView("sala-de-espera",modelMap);
    }


}
