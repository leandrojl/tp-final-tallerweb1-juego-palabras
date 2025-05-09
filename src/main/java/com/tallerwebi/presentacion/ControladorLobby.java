package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class ControladorLobby {


    @RequestMapping("/lobby")
    public ModelAndView Lobby(HttpSession session, Model model) {
        Jugador jugador = (Jugador) session.getAttribute("jugador");

        if (jugador != null) {
            model.addAttribute("jugador", jugador);
        } else {
            jugador = new Jugador(); // ← Crear nuevo jugador antes de usarlo
            jugador.setNombre("july3p");
            model.addAttribute("jugador", jugador);
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
