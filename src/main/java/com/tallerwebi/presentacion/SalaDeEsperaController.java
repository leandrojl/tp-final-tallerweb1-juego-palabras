package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class SalaDeEsperaController {

    private SalaDeEsperaService salaDeEsperaService;
    private UsuarioService usuarioService;
    private UsuarioPartidaService usuarioPartidaService;


    @Autowired
    public SalaDeEsperaController(SalaDeEsperaService servicioSalaDeEspera,
                                  UsuarioService usuarioService,
                                  UsuarioPartidaService usuarioPartidaService) {
        this.salaDeEsperaService = servicioSalaDeEspera;
        this.usuarioService = usuarioService;
        this.usuarioPartidaService = usuarioPartidaService;
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

    @RequestMapping("/agregarJugadorALaSalaDeEspera")
    public ModelAndView agregarJugadorALaSalaDeEspera(Jugador jugador) {
        ModelMap modelo = new ModelMap();
        modelo.put("jugador1",jugador.getNombre());
        return new ModelAndView("sala-de-espera", modelo);
    }

    @GetMapping("/sala-de-espera/{idPartida}")
    public String salaDeEspera(@PathVariable Long idPartida, Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String nombreUsuario = usuarioService.obtenerNombrePorId(usuarioId);

        model.addAttribute("usuario", nombreUsuario);
        model.addAttribute("idPartida", idPartida);

        return "sala-de-espera";
    }

    @RequestMapping(value = "/sala-de-espera", method = RequestMethod.GET)
    public String armarLaSalaDeEspera(Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        Long idPartida = (Long) session.getAttribute("idPartida");

        String nombreUsuario = usuarioPartidaService.obtenerNombreDeUsuarioEnLaPartida(usuarioId,idPartida);
        System.out.println(nombreUsuario);

        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("usuario", nombreUsuario);
        model.addAttribute("idPartida", idPartida);

        return "sala-de-espera";
    }

    @PostMapping("/sala-de-espera")
    public String recibirIdPartida(@RequestParam Long idPartida, HttpSession session) {
        session.setAttribute("idPartida", idPartida);
        return "redirect:/sala-de-espera";
    }



    //WEBSOCKETS EN SALA DE ESPERA

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

    @MessageMapping("/inicioPartida")
    public void enviarUsuariosALaPartida(MensajeRecibidoDTO mensajeRecibidoDTO) {

        this.salaDeEsperaService.redireccionarUsuariosAPartida(mensajeRecibidoDTO);
    }
}
