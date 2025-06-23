package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
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

    private SalaDeEsperaService salaDeEsperaService;

    @Autowired
    public SalaDeEsperaController(SalaDeEsperaService servicioSalaDeEspera) {
        this.salaDeEsperaService = servicioSalaDeEspera;
    }

    public SalaDeEsperaController() {
        // Constructor vacío
    }

    @RequestMapping("/iniciarPartida")
    public ModelAndView iniciarPartida(@RequestParam Map<String, String> parametros) {

        ModelMap model = new ModelMap();

        Map<Long, Boolean> jugadores = salaDeEsperaService.obtenerJugadoresDelFormulario(parametros);

        List<Long> jugadoresNoListos = salaDeEsperaService.verificarSiHayJugadoresQueNoEstenListos(jugadores);


        model.put("jugadores", jugadores);

        return new ModelAndView("redirect:/juego?jugadorId=1"); //hardcodeado por ahora
    }


    //WEBSOCKETS EN SALA DE ESPERA


    @RequestMapping("/agregarJugadorALaSalaDeEspera")
    public ModelAndView agregarJugadorALaSalaDeEspera(Jugador jugador) {
        ModelMap modelo = new ModelMap();
        modelo.put("jugador1",jugador.getNombre());
        return new ModelAndView("sala-de-espera", modelo);
    }


    @MessageMapping("/salaDeEspera")
    @SendTo("/topic/salaDeEspera")
    public EstadoJugadorDTO actualizarEstadoJugador(EstadoJugadorDTO estadoJugadorDTO, Principal principal) {
        //Si un usuario intenta cambiar el estado que no es suyo se valida
        String nombreUsuario = estadoJugadorDTO.getUsername();
        if(!nombreUsuario.equals(principal.getName())) {
            throw new UsuarioInvalidoException("Error, no se puede alterar el estado de otro jugador");
        }
        return estadoJugadorDTO;
    }

    @MessageExceptionHandler(UsuarioInvalidoException.class)
    @SendToUser("/queue/mensajeAlIntentarCambiarEstadoDeOtroJugador")
    public MensajeRecibidoDTO handleUsuarioInvalidoException(UsuarioInvalidoException ex) {
        return new MensajeRecibidoDTO(ex.getMessage());
    }


    @MessageMapping("/usuarioSeUneASalaDeEspera")
    @SendTo("/topic/cuandoUsuarioSeUneASalaDeEspera")
    public MensajeRecibidoDTO usuarioSeUneASala(MensajeRecibidoDTO mensajeRecibidoDTO, Principal principal){
        String nombreUsuario = principal.getName();
        this.salaDeEsperaService.mostrarAUnUsuarioLosUsuariosExistentesEnSala(nombreUsuario);
        return new MensajeRecibidoDTO(nombreUsuario);
    }
}
