package com.tallerwebi.presentacion;


import com.tallerwebi.dominio.excepcion.CantidadDeUsuariosInsuficientesException;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Jugador;

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
    public ModelAndView iniciarPartida(@RequestParam Map<String, String> parametros, HttpSession session) {

        String nombreUsuario = (String) session.getAttribute("usuario");   // nombre de usuario como String
        Long usuarioId = (Long) session.getAttribute("usuarioID");          // id usuario como Long

        if (nombreUsuario == null || usuarioId == null) {
            // Si no hay usuario en sesión, redirigir al login
            return new ModelAndView("redirect:/login");
        }

        Map<Long, Boolean> jugadores = salaDeEsperaService.obtenerJugadoresDelFormulario(parametros);

        List<Long> jugadoresNoListos = salaDeEsperaService.verificarSiHayJugadoresQueNoEstenListos(jugadores);


        if (!jugadoresNoListos.isEmpty()) {
            ModelMap model = new ModelMap();
            model.put("jugadores", jugadores);
            model.put("mensajeError", "No todos los jugadores están listos");
            return new ModelAndView("sala-de-espera", model);
        }

        // Redirigir a /juego pasando el usuarioID de sesión
        return new ModelAndView("redirect:/juego");
    }

    @RequestMapping("/agregarJugadorALaSalaDeEspera")
    public ModelAndView agregarJugadorALaSalaDeEspera(Jugador jugador) {
        ModelMap modelo = new ModelMap();
        modelo.put("jugador1",jugador.getNombre());
        return new ModelAndView("sala-de-espera", modelo);
    }

    @GetMapping("/sala-de-espera/{idPartida}")
    public String salaDeEspera(@PathVariable Long idPartida, Model model, HttpSession session) {
        Long usuarioId = (Long) session.getAttribute("idUsuario");
        String nombreUsuario = usuarioService.obtenerNombrePorId(usuarioId);

        model.addAttribute("usuario", nombreUsuario);
        model.addAttribute("idPartida", idPartida);

        return "sala-de-espera";
    }

    @RequestMapping(value = "/sala-de-espera", method = {RequestMethod.GET, RequestMethod.POST})
    public String manejarSalaDeEspera(
            @RequestParam(required = false) Long idPartida,
            Model model,
            HttpSession session
    ) {
        if (idPartida != null) {
            session.setAttribute("idPartida", idPartida);
            System.out.println("SOY EL ID PARTIDA PA del qe SEUNA A LA SALA PAAAAA"+idPartida);
        } else {
            idPartida = (Long) session.getAttribute("idPartida");
        }

        Long idUsuario = (Long) session.getAttribute("idUsuario");
        UsuarioPartida existeRegistro = usuarioPartidaService.buscarUsuarioPartida(idPartida,
                idUsuario);
        if(existeRegistro == null){// DONDE SE EVITA QUE SE DUPLIQUE USUARIOPARTIDA
            usuarioPartidaService.agregarUsuarioAPartida(idUsuario,idPartida,0,false,Estado.EN_ESPERA);
        }
        String nombreUsuario = usuarioService.obtenerNombrePorId(idUsuario);
        model.addAttribute("idUsuario", idUsuario);
        model.addAttribute("usuario", nombreUsuario);
        model.addAttribute("idPartida", idPartida);

        return "sala-de-espera";
    }




    //WEBSOCKETS EN SALA DE ESPERA

    @MessageMapping("/salaDeEspera")
    public void actualizarEstadoUsuario(EstadoJugadorDTO estadoJugadorDTO, Principal principal) {
        //Si un usuario intenta cambiar el estado que no es suyo se valida
        Boolean correcto = this.salaDeEsperaService.actualizarElEstadoDeUnUsuario(estadoJugadorDTO, principal.getName());
        if(!correcto){
            throw new UsuarioInvalidoException("Error, no se puede alterar el estado de otro jugador");
        }
    }

    @MessageExceptionHandler(UsuarioInvalidoException.class)
    @SendToUser("/queue/mensajeAlIntentarCambiarEstadoDeOtroJugador")
    public MensajeRecibidoDTO handleUsuarioInvalidoException(UsuarioInvalidoException ex) {
        return new MensajeRecibidoDTO(ex.getMessage());
    }


    @MessageMapping("/usuarioSeUneASalaDeEspera")
    public void usuarioSeUneASala(MensajeRecibidoDTO mensajeRecibidoDTO, Principal principal){
        String nombreUsuario = principal.getName();
        Long idPartida = mensajeRecibidoDTO.getNumber();
        this.salaDeEsperaService.mostrarAUnUsuarioLosUsuariosExistentesEnSala(nombreUsuario,idPartida);
    }

    @MessageMapping("/inicioPartida")
    public void enviarUsuariosALaPartida(MensajeRecibidoDTO mensajeRecibidoDTO) {
        Boolean redireccionamientoCorrecto = this.salaDeEsperaService.redireccionarUsuariosAPartida(mensajeRecibidoDTO);
        if(!redireccionamientoCorrecto){ //FUNCIONALIDAD PARA SPRINT 4 DE MINIMA CANT DE JUGADORES PARA INICIAR
            // PARTIDA REQUERIDA
            throw new CantidadDeUsuariosInsuficientesException("Cantidad insuficiente de usuarios para iniciar " +
                    "partida");
        }
    }

    //PARA SPRINT 4

    @MessageExceptionHandler(CantidadDeUsuariosInsuficientesException.class)
    @SendTo("/topic/noSePuedeIrALaPartida")
    public MensajeRecibidoDTO enviarMensajeDeDenegacionDeAvanceAPartida(CantidadDeUsuariosInsuficientesException ex) {
        return new MensajeRecibidoDTO(ex.getMessage());
    }

    @MessageMapping("/abandonarSala")
    @SendToUser("/queue/alAbandonarSala")
    public MensajeRecibidoDTO abandonarSala(MensajeDto mensajeDto, Principal principal) {
        return this.salaDeEsperaService.abandonarSala(mensajeDto,principal.getName());
    }

    @MessageExceptionHandler(Exception.class)
    public void paraExcepciones(Exception ex) {
        ex.printStackTrace();
    }
}
