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
public class RegistroController {

    private List<Jugador> jugadores = Arrays.asList(
            new Jugador("pepe123", "pepe@gmail.com", "abc123245"),
            new Jugador("lucas", "lucas@gmail.com", "12151gdsf"),
            new Jugador("nicolas", "nicolas@gmail.com", "bv2b132v1")
    );

    @RequestMapping("/registro")
    public ModelAndView mostrarRegistro() {
        return new ModelAndView("registro");
    }

    public ModelAndView RedireccionarAVistaLogin() {
        return new ModelAndView("login");
    }

    @PostMapping("/procesarRegistro")
    public ModelAndView registrar(@ModelAttribute Jugador jugador) {
        ModelMap model = new ModelMap();
        if(jugador.getUsuario().isEmpty()){
            model.addAttribute("error", "El usuario no puede estar vacio");
            return new ModelAndView("registro", model);
        }
        if(jugador.getEmail().isEmpty()){
            model.addAttribute("error", "El email no puede estar vacio");
            return new ModelAndView("registro", model);
        }
        if(jugador.getPassword().isEmpty()){
            model.addAttribute("error", "La contrasenia no puede estar vacia");
            return new ModelAndView("registro", model);
        }
        if(buscarJugador(jugador.getUsuario()) != null){
            model.addAttribute("error", "El usuario ya existe");
            return new ModelAndView("registro", model);
        }
        return new ModelAndView("login");
    }

    private Jugador buscarJugador(String usuario) {
        for (Jugador jugador : jugadores) {
            if (jugador.getUsuario().equals(usuario)) {
                return jugador;
            }
        }
        return null;
    }

}
