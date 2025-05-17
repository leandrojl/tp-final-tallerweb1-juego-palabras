package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorVistaFinal {

    @RequestMapping("/vistaFinal")
    public ModelAndView verPantallaFinal(String nombre, Integer puntos) {

        ModelAndView mav = new ModelAndView("vistaFinalJuego");
        mav.addObject("nombre", nombre);
        mav.addObject("puntos", puntos);
        return mav;
    }

}
