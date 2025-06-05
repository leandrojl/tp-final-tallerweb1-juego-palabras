package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PerfilController {


    @RequestMapping(value = "/perfil")
    public ModelAndView irAPerfil(){
    ModelMap modelo = new ModelMap();
    modelo.put("nombre", "Juan Perez");
    modelo.put("usuario", "Juancito123");
    modelo.put("edad", "15");
    modelo.put("winrate", "70%");

    modelo.put("fotoPerfil", "/resources/images/fotoperfil1.png");



        return new ModelAndView("perfil", modelo);
    }


}
