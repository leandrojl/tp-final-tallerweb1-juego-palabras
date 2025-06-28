package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.PerfilService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@Controller
public class PerfilController {
    private PerfilService perfilService;

    @Autowired
    public PerfilController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }

    @RequestMapping(value = "/perfil")
    public ModelAndView irAPerfil(HttpSession session) {
        ModelMap modelo = new ModelMap();
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        Usuario usuario = perfilService.obtenerDatosDelPerfilPorId(usuarioId);
       /* modelo.addAllAttributes(perfilService.obtenerDatosDePerfil(usuario));*/
        modelo.addAttribute("usuario", usuario);

        return new ModelAndView("perfil", modelo);
    }


}