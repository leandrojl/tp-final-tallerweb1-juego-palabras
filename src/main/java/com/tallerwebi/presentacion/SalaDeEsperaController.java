package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SalaDeEsperaController {

    private SalaDeEsperaService servicioSalaDeEspera;
    private PartidaService partidaService;
    private RondaService rondaService;

    @Autowired
    public SalaDeEsperaController(SalaDeEsperaService servicioSalaDeEspera,PartidaService partidaService,RondaService rondaService) {
        this.servicioSalaDeEspera = servicioSalaDeEspera;
        this.partidaService = partidaService;
        this.rondaService = rondaService;
    }

    public SalaDeEsperaController() {
        // Constructor vacío
    }



    @RequestMapping("/iniciarPartida")
    public ModelAndView iniciarPartida(@RequestParam Map<String, String> parametros, HttpSession session) {

        String nombreUsuario = (String) session.getAttribute("usuario");   // nombre de usuario como String
        Long usuarioId = (Long) session.getAttribute("usuarioID");          // id usuario como Long

        if (nombreUsuario == null || usuarioId == null) {
            // Si no hay usuario en sesión, redirigir al login
            return new ModelAndView("redirect:/login");
        }

        Map<Long, Boolean> jugadores = servicioSalaDeEspera.obtenerJugadoresDelFormulario(parametros);
        List<Long> jugadoresNoListos = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);

        if (!jugadoresNoListos.isEmpty()) {
            ModelMap model = new ModelMap();
            model.put("jugadores", jugadores);
            model.put("mensajeError", "No todos los jugadores están listos");
            return new ModelAndView("sala-de-espera", model);
        }

        // Redirigir a /juego pasando el usuarioID de sesión
        return new ModelAndView("redirect:/juego?jugadorId=" + usuarioId);
    }










    @RequestMapping("/agregarJugadorALaSalaDeEspera")
    public ModelAndView agregarJugadorALaSalaDeEspera(Jugador jugador) {
        ModelMap modelo = new ModelMap();
        modelo.put("jugador1",jugador.getNombre());
        return new ModelAndView("sala-de-espera", modelo);
    }
}
