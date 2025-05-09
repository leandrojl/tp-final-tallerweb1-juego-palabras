package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
public class LoginController {
    private List<Jugador> jugadores = Arrays.asList(
            new Jugador("pepe1235421", "pepe@gmail.com", "abc123245"),
            new Jugador("lucas", "lucas@gmail.com", "12151gdsf"),
            new Jugador("nicolas", "nicolas@gmail.com", "bv2b132v1")
    );

    @RequestMapping("/login")
    public ModelAndView mostrarLogin() {
        ModelMap model = new ModelMap();
        model.addAttribute("jugador", new Jugador());
        return new ModelAndView("login",model);
    }


    public ModelAndView redireccionarAVistaRegistro() {
        return new ModelAndView("registro");
    }

    @PostMapping("/procesarLogin")
    public ModelAndView login(@ModelAttribute Jugador jugador) {
        ModelMap modelMap = new ModelMap();
        Jugador j = buscarJugador(jugador.getUsuario());
        if(jugador.getUsuario().isEmpty()){
            modelMap.addAttribute("error","El campo de usuario no puede estar vacio");
            return new ModelAndView("login", modelMap);
        }
        if(jugador.getPassword().isEmpty()){
            modelMap.addAttribute("error","El campo de contraseña no puede estar vacio");
            return new ModelAndView("login", modelMap);
        }
        if(j == null){
            modelMap.addAttribute("error","El usuario no existe");
            return new ModelAndView("login", modelMap);
        }
        if(!j.getPassword().equals(jugador.getPassword())){
            modelMap.addAttribute("error","La contraseña no es correcta");
            return new ModelAndView("login", modelMap);
        }
        modelMap.addAttribute("jugador", jugador);
        return new ModelAndView("lobby",modelMap);
    }

    private Jugador buscarJugador(String usuario) {
        for(Jugador j : jugadores){
            if(j.getUsuario().equals(usuario)){
                return j;
            }
        }
        return null;
    }
}
