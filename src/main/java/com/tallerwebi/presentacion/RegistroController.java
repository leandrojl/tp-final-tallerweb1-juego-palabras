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

    //En el futuro mandarlo a un servicio
    private List<Jugador> jugadores = Arrays.asList(
            new Jugador("pepe1235421", "pepe@gmail.com", "abc123245"),
            new Jugador("lucas", "lucas@gmail.com", "12151gdsf"),
            new Jugador("nicolas", "nicolas@gmail.com", "bv2b132v1")
    );

    @RequestMapping("/registro")
    public ModelAndView mostrarRegistro() {
        ModelMap model = new ModelMap();
        model.addAttribute("jugador", new Jugador());
        return new ModelAndView("registro",model);
    }

    public ModelAndView RedireccionarAVistaLogin() {
        return new ModelAndView("login");
    }

    @PostMapping("/procesarRegistro")
    public ModelAndView registrar(@ModelAttribute Jugador jugador) {
        ModelMap model = new ModelMap();
        //Crear clase validadora de campos para no tener ifs aca
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

        //Efectuar el registro una vez validados los campos
        //Retornar a la vista login cuando ya se realizo el registro
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
