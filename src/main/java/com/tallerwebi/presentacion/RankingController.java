package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.model.Usuario;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

public class RankingController {

    @RequestMapping(value = "/verRanking")
    public ModelAndView verRanking() {
            ModelMap modelo = new ModelMap();
           // modelo.addAllAttributes();
    return new ModelAndView("ranking", modelo);
    }
}
