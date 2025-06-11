package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ResultadosController {

    @RequestMapping("/vistaFinal")
    public ModelAndView verPantallaFinal(@RequestParam String nombre,@RequestParam Integer puntos) {

        ModelAndView mav = new ModelAndView("resultados");
        mav.addObject("nombre", nombre);
        mav.addObject("puntos", puntos);
        return mav;
    }

}
