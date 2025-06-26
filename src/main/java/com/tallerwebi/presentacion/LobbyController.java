package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

@Controller
public class LobbyController {

    private LobbyService lobbyService;
    private Partida2Service partida2Service;
    private UsuarioService usuarioService;
    private UsuarioPartidaService usuarioPartidaService;

    @Autowired
    public LobbyController(Partida2Service partida2Service,
                           LobbyService lobbyService,
                           UsuarioService usuarioService,
                           UsuarioPartidaService usuarioPartidaService) {
        this.partida2Service = partida2Service;
        this.lobbyService = lobbyService;
        this.usuarioService = usuarioService;
        this.usuarioPartidaService = usuarioPartidaService;
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

        Partida2 nuevaPartida = new Partida2(nombre, idioma, permiteComodin, rondasTotales, maximoJugadores, minimoJugadores, Estado.EN_ESPERA);
        Serializable idPartida = partida2Service.crearPartida(nuevaPartida);
        //Estado estadoPartida = partida2Service.buscarEstadoPartida((Long) idPartida);
        int puntaje = 0;
        boolean gano = false;
        usuarioPartidaService.agregarUsuarioAPartida(idUsuario, (Long) idPartida,puntaje,gano, Estado.EN_ESPERA);
        System.out.println("ID de la partida creada: " + idPartida);
        session.setAttribute("idPartida", idPartida);

        return "redirect:/sala-de-espera";
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
        List<Partida2> partidas = lobbyService.obtenerPartidasEnEspera();
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
