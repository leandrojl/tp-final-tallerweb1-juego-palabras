package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class ControladorPerfil {
private ServicioPerfil servicioPerfil;

@Autowired
public ControladorPerfil(ServicioPerfil servicioPerfil) {
    this.servicioPerfil = servicioPerfil;
}

    @RequestMapping(value = "/perfil")
    public ModelAndView irAPerfil(){
    ModelMap modelo = new ModelMap();
    modelo.addAllAttributes(servicioPerfil.obtenerDatosDePerfil());




    return new ModelAndView("perfil", modelo);
    }


}
