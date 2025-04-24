package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorSalaDeEspera {

    @RequestMapping("/salaDeEspera")
    public ModelAndView salaDeEspera() {

        ModelMap modelo = new ModelMap();

        modelo.put("jugador1","Jugador 1 - Pepito");
        modelo.put("jugador2","Jugador 2 - Alfonso");

        return new ModelAndView("sala-de-espera", modelo);
    }

    @RequestMapping("/iniciarPartida")
    public ModelAndView iniciarPartida() {


        return new ModelAndView("partida-en-desarrollo");
    }


}
