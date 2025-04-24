package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorLobby {


    @RequestMapping("/lobby")
    public ModelAndView Lobby(){
        return new ModelAndView("lobby");

    }


    @RequestMapping("/irRanking")
    public ModelAndView Ranking() {
        return new ModelAndView("ranking");    }


    @RequestMapping("/irPerfil")
    public ModelAndView Perfil() {
        return new ModelAndView("perfil");
    }

    @RequestMapping("/irPartida")
    public ModelAndView Partida() {
        return new ModelAndView("partida");
    }

    @RequestMapping("/irRecompensas")
    public ModelAndView Recompensa() {
        return new ModelAndView("recompensas");
    }




}
