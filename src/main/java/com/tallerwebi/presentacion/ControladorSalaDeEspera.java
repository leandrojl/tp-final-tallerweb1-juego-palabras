package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ControladorSalaDeEspera {

    @RequestMapping("/iniciarPartida")
    public ModelAndView iniciarPartida(@RequestParam Map<String, String> parametros) {

        ModelMap model = new ModelMap();

        Map<Long, Boolean> jugadores = new HashMap<>();

        List<Long> jugadoresNoListos = new ArrayList<>();

        parametros.forEach((clave, valor) -> {
            if (clave.startsWith("jugador_")) {
                Long jugadorId = Long.parseLong(clave.replace("jugador_", ""));
                Boolean listo = Boolean.parseBoolean(valor);
                jugadores.put(jugadorId, listo);
            }
        });

        for (Map.Entry<Long, Boolean> entry : jugadores.entrySet()) {
            Long jugadorId = entry.getKey();
            Boolean listo = entry.getValue();

            if (!listo) {
                jugadoresNoListos.add(jugadorId);
            }
        }

        if (!jugadoresNoListos.isEmpty()) {
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
            model.put("usuarios", usuarios);
            model.put("error", "Los siguientes jugadores no están listos: " + jugadoresNoListos);
            return new ModelAndView("sala-de-espera", model);
        }

        model.put("jugadores", jugadores);
        // Redirigir o devolver una vista
        return new ModelAndView("redirect:/juego?jugadorId=1"); //hardcodeado por ahora
    }

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



    @RequestMapping("/agregarJugadorALaSalaDeEspera")
    public ModelAndView agregarJugadorALaSalaDeEspera(Jugador jugador) {
        ModelMap modelo = new ModelMap();
        modelo.put("jugador1",jugador.getNombre());
        return new ModelAndView("sala-de-espera", modelo);
    }
}
