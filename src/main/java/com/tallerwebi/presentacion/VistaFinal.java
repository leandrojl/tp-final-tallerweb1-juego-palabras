package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VistaFinal {

    @RequestMapping("/vistaFinal")
    public ModelAndView verPantallaFinal() {
        String nombreGanador = "Pepito";
        String puntos = "500";
        ModelAndView mav = new ModelAndView("vistaFinalJuego");
        mav.addObject("nombre", nombreGanador);
        mav.addObject("puntos", puntos);
        return mav;
    }

}
