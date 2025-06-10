package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.LobbyService;
import com.tallerwebi.dominio.PartidaService;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LobbyController {


    private PartidaService partidaService;
    private LobbyService lobbyService;

    @Autowired
    public LobbyController(PartidaService partidaService, LobbyService lobbyService) {
        this.partidaService = partidaService;
        this.lobbyService = lobbyService;
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

           //momentaneamente se crean partidas en espera de ejemplo
            lobbyService.guardar(new Partida2("Partida en espera 1", "Ingles", true, 5, 2,6, Estado.EN_ESPERA));
            lobbyService.guardar(new Partida2("Partida en espera 2", "Ingles", false, 3, 4,6, Estado.EN_ESPERA));
            lobbyService.guardar(new Partida2("Partida en espera 3", "Ingles", true, 7, 3,6, Estado.EN_ESPERA));

            // obtengo las partidas en espera
            List<Partida2> partidas = lobbyService.obtenerPartidasEnEspera();
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




}
