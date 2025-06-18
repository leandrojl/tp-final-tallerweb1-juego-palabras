package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.PerfilService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;



@Controller
public class PerfilController {
    private PerfilService perfilService;

    @Autowired
    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @RequestMapping(value = "/perfil")
    public ModelAndView irAPerfil(){
        ModelMap modelo = new ModelMap();
        Usuario usuario = new Usuario("Juan", "asd@asd", "asdd1234", 5);
        modelo.addAllAttributes(perfilService.obtenerDatosDePerfil(usuario));


        return new ModelAndView("perfil", modelo);
    }


}