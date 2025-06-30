package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.interfaceService.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    private LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }


    @RequestMapping("/login")
    public ModelAndView mostrarLogin() {
        ModelMap model = new ModelMap();
        model.addAttribute("usuario",new Usuario());
        return new ModelAndView("login",model);
    }


    public ModelAndView redireccionarAVistaRegistro() {
        return new ModelAndView("registro");
    }

    @PostMapping("/procesarLogin")
    public ModelAndView login(@ModelAttribute Usuario usuario, HttpSession session) {
        ModelMap modelMap = new ModelMap();
        if(usuario.getNombreUsuario().isEmpty()){
            modelMap.addAttribute("error","El campo de usuario no puede estar vacio");
            return new ModelAndView("login", modelMap);
        }
        if(usuario.getPassword().isEmpty()){
            modelMap.addAttribute("error","El campo de contraseña no puede estar vacio");
            return new ModelAndView("login", modelMap);
        }
        try{
            Usuario usuarioLogueado = this.loginService.login(usuario.getNombreUsuario(), usuario.getPassword());
            modelMap.addAttribute("Usuario", usuarioLogueado);
            session.setAttribute("usuario",usuarioLogueado.getNombreUsuario());
            session.setAttribute("usuarioId", usuarioLogueado.getId());
            return new ModelAndView("redirect:/lobby");
        }catch(DatosLoginIncorrectosException datosLoginIncorrectos){
            modelMap.addAttribute("error","Datos incorrectos");
            return new ModelAndView("login", modelMap);
        }
    }
}