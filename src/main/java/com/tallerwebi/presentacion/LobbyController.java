package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.interfaceService.Partida2Service;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LobbyController {


    private PartidaService partidaService;
    private LobbyService lobbyService;
    private Partida2Service partida2Service;

    @Autowired
    public LobbyController(Partida2Service partida2Service, LobbyService lobbyService) {
        this.partida2Service = partida2Service;
        this.lobbyService = lobbyService;
    }
    @GetMapping("/buscar")
    @ResponseBody
    public List<Partida2> buscar(@RequestParam("nombre") String nombre) {
        System.out.println(nombre);


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
            @RequestParam int minimoJugadores
    ) {
        // Aquí deberías crear la sala usando tu servicio, por ejemplo:
        Partida2 nuevaPartida = new Partida2(nombre, idioma, permiteComodin, rondasTotales, maximoJugadores, minimoJugadores, Estado.EN_ESPERA);
        partida2Service.crearPartida(nuevaPartida);

        // Redirige al lobby después de crear la sala
        return "redirect:/lobby";
    }


    @RequestMapping("/lobby")
    public ModelAndView Lobby(HttpSession session, Model model) {
        Jugador jugador = (Jugador) session.getAttribute("jugador");

        if (jugador != null) {
            model.addAttribute("jugador", jugador);
        } else {
            jugador = new Jugador();
            jugador.setNombre("july3p");
            model.addAttribute("jugador", jugador);
            //session.setAttribute("usuario", "pepe");

            // obtengo las partidas en espera
            List<Partida2> partidas = lobbyService.obtenerPartidasEnEspera();
            if (partidas.isEmpty()) {
                model.addAttribute("mensaje", "No hay partidas disponibles en curso.");
            } else {
                model.addAttribute("partidas", partidas);
            }
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
