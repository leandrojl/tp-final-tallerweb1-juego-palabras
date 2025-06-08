package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.PasswordMenorAOchoCaracteresException;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.RegistroService;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegistroController {

    private RegistroService registroService;
    //En el futuro mandarlo a un servicio

    @Autowired
    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    @RequestMapping("/registro")
    public ModelAndView mostrarRegistro() {
        ModelMap model = new ModelMap();
        model.addAttribute("usuario", new Usuario());
        return new ModelAndView("registro",model);
    }

    public ModelAndView RedireccionarAVistaLogin() {
        return new ModelAndView("login");
    }

    @PostMapping("/procesarRegistro")
    public ModelAndView registrar(@ModelAttribute Usuario usuario) {
        ModelMap model = new ModelMap();
        //Crear clase validadora de campos para no tener ifs aca
        if(usuario.getNombreUsuario().isEmpty()){
            model.addAttribute("error", "El usuario no puede estar vacio");
            return new ModelAndView("registro", model);
        }
        if(usuario.getPassword().isEmpty()){
            model.addAttribute("error", "La contrasenia no puede estar vacia");
            return new ModelAndView("registro", model);
        }
        try{
            this.registroService.registrar(usuario.getNombreUsuario(), usuario.getPassword());
            model.addAttribute("mensaje", "Usuario registrado correctamente");
            return new ModelAndView("login", model);
        }catch(UsuarioExistenteException u){
            model.addAttribute("error", "El usuario ya existe");
            return new ModelAndView("registro", model);
        }catch(PasswordMenorAOchoCaracteresException p){
            model.addAttribute("error", "La contraseña debe contener al menos ocho caracteres");
            return new ModelAndView("registro", model);
        }
    }

}
