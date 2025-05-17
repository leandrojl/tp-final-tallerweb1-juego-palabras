package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ControladorSalaDeEspera {

    @RequestMapping("/salaDeEspera")
    public ModelAndView salaDeEspera() {
        ModelMap modelo = new ModelMap();
        //dado que tengo una lista de usuarios con usuarios
        List<Usuario> usuarios = new ArrayList<>();
        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();
        usuario1.setNombre("Pepitoxx");
        usuario2.setNombre("Alfonsoww");
        usuario1.setId(1L);
        usuario2.setId(2L);

        //agrego los usuarios a la lista
        usuarios.add(usuario1);
        usuarios.add(usuario2);

        //agrego la lista de usuarios al modelo
        modelo.put("usuarios", usuarios);

        return new ModelAndView("sala-de-espera", modelo);
    }

    @RequestMapping("/iniciarPartida")
    public ModelAndView iniciarPartida() {


        return new ModelAndView("juego");
    }


    @RequestMapping("/agregarJugadorALaSalaDeEspera")
    public ModelAndView agregarJugadorALaSalaDeEspera(Jugador jugador) {
        ModelMap modelo = new ModelMap();
        modelo.put("jugador1",jugador.getNombre());
        return new ModelAndView("sala-de-espera", modelo);
    }
}
