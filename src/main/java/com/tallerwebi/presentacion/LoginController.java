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
import java.util.Random;

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

    // src/main/java/com/tallerwebi/presentacion/LoginController.java

    @PostMapping("/jugar-rapido")
    public ModelAndView jugarRapido(@ModelAttribute Usuario usuario, HttpSession session) {
        ModelMap modelMap = new ModelMap();
        String nombreUsuario = usuario.getNombreUsuario();

        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            modelMap.addAttribute("error_rapido", "El nombre de usuario no puede estar vacío.");
            modelMap.addAttribute("usuario", new Usuario());
            return new ModelAndView("login", modelMap);
        }

        if (loginService.buscarUsuario(nombreUsuario) != null) {
            modelMap.addAttribute("error_rapido", "El nombre de usuario ya está en uso. Elige otro.");
            modelMap.addAttribute("usuario", new Usuario());
            return new ModelAndView("login", modelMap);
        }

        try {
            // Crear y guardar el nuevo usuario para juego rápido
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setNombreUsuario(nombreUsuario);

            // Generar un número aleatorio entre 1000 y 9999
            Random random = new Random();
            int numeroAleatorio = 1000 + random.nextInt(9000);

            // Se asigna una contraseña por defecto con número aleatorio
            nuevoUsuario.setPassword(nombreUsuario + numeroAleatorio);

            // 1. Guardar el nuevo usuario en la base de datos usando el método simple del LoginService
            loginService.registrar(nuevoUsuario);

            // 2. Iniciar sesión directamente
            session.setAttribute("usuario", nuevoUsuario.getNombreUsuario());
            session.setAttribute("idUsuario", nuevoUsuario.getId());

            return new ModelAndView("redirect:/lobby");
        } catch (Exception e) {
            // Captura cualquier otro error que pueda ocurrir al guardar en la BD
            modelMap.addAttribute("error_rapido", "Ocurrió un error inesperado al crear el usuario.");
            modelMap.addAttribute("usuario", new Usuario());
            return new ModelAndView("login", modelMap);
        }
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
            session.setAttribute("idUsuario", usuarioLogueado.getId());
            return new ModelAndView("redirect:/lobby");
        }catch(DatosLoginIncorrectosException datosLoginIncorrectos){
            modelMap.addAttribute("error",datosLoginIncorrectos.getMessage());
            return new ModelAndView("login", modelMap);
        }
    }
}