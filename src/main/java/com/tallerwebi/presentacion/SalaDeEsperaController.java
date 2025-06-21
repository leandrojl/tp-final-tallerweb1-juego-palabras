package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SalaDeEsperaController {

    private SalaDeEsperaService servicioSalaDeEspera;

    @Autowired
    public SalaDeEsperaController(SalaDeEsperaService servicioSalaDeEspera) {
        this.servicioSalaDeEspera = servicioSalaDeEspera;
    }

    public SalaDeEsperaController() {
        // Constructor vacío
    }

    @RequestMapping("/iniciarPartida")
    public ModelAndView iniciarPartida(@RequestParam Map<String, String> parametros) {

        ModelMap model = new ModelMap();

        Map<Long, Boolean> jugadores = servicioSalaDeEspera.obtenerJugadoresDelFormulario(parametros);

        List<Long> jugadoresNoListos = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);


        model.put("jugadores", jugadores);

        return new ModelAndView("redirect:/juego?jugadorId=1"); //hardcodeado por ahora
    }




    @RequestMapping("/agregarJugadorALaSalaDeEspera")
    public ModelAndView agregarJugadorALaSalaDeEspera(Jugador jugador) {
        ModelMap modelo = new ModelMap();
        modelo.put("jugador1",jugador.getNombre());
        return new ModelAndView("sala-de-espera", modelo);
    }
}
