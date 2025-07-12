package com.tallerwebi.presentacion;


import com.tallerwebi.dominio.DTO.EstadoJugadorDTO;
import com.tallerwebi.dominio.DTO.MensajeDto;
import com.tallerwebi.dominio.DTO.MensajeRecibidoDTO;
import com.tallerwebi.dominio.excepcion.CantidadDeUsuariosInsuficientesException;
import com.tallerwebi.dominio.excepcion.CantidadDeUsuariosListosInsuficientesException;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Jugador;

import com.tallerwebi.dominio.model.UsuarioPartida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private PartidaService partidaService;

    @Autowired
    public SalaDeEsperaController(SalaDeEsperaService servicioSalaDeEspera,
                                  UsuarioService usuarioService,
                                  UsuarioPartidaService usuarioPartidaService, PartidaService partidaService) {
        this.salaDeEsperaService = servicioSalaDeEspera;
        this.usuarioService = usuarioService;
        this.usuarioPartidaService = usuarioPartidaService;
        this.partidaService = partidaService;
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
        Long idUsuario = (Long) session.getAttribute("idUsuario");
        String nombreUsuario = usuarioService.obtenerNombrePorId(idUsuario);
        boolean esCreador = partidaService.verificarSiEsElCreadorDePartida(idUsuario, idPartida);

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

        } else {
            idPartida = (Long) session.getAttribute("idPartida");
        }
        Long idUsuario = (Long) session.getAttribute("idUsuario");
            Estado estadoDeLaPartida = partidaService.verificarEstadoDeLaPartida(idPartida);
            if (estadoDeLaPartida == Estado.EN_ESPERA) {
                UsuarioPartida existeRegistro = usuarioPartidaService.buscarUsuarioPartida(idPartida,
                idUsuario);
                if(existeRegistro == null){// DONDE SE EVITA QUE SE DUPLIQUE USUARIOPARTIDA
                usuarioPartidaService.agregarUsuarioAPartida(idUsuario,idPartida,0,false,Estado.EN_ESPERA);
                } else if (existeRegistro.getEstado() == Estado.CANCELADA) {
                    // (NUEVO) Si existe pero fue cancelado (expulsado), lo reactivamos
                    usuarioPartidaService.cambiarEstado(idUsuario, idPartida, Estado.EN_ESPERA);
                }
                //}
                boolean esCreador = partidaService.verificarSiEsElCreadorDePartida(idUsuario, idPartida);

                String nombreUsuario = usuarioService.obtenerNombrePorId(idUsuario);
                String nombrePartida = partidaService.obtenerNombrePartidaPorId(idPartida);
                model.addAttribute("idUsuario", idUsuario);
                model.addAttribute("usuario", nombreUsuario);
                model.addAttribute("idPartida", idPartida);
                model.addAttribute("esCreador", esCreador);
                model.addAttribute("nombrePartida", nombrePartida);

                return "sala-de-espera";

            }

            return "redirect:/lobby"; // Redirigir a la página de juego si la partida ya está en curso
    }


    //WEBSOCKETS EN SALA DE ESPERA

    @MessageMapping("/expulsarDeSala")
    public void expulsarDeSala(MensajeDto mensajeDto, Principal principal) {
        String nombreUsuarioDelPrincipal = principal.getName();
        this.salaDeEsperaService.expulsarJugador(mensajeDto, nombreUsuarioDelPrincipal);
    }

    @MessageMapping("/salaDeEspera")
    public void actualizarEstadoUsuario(EstadoJugadorDTO estadoJugadorDTO, Principal principal) {
        this.salaDeEsperaService.actualizarElEstadoDeUnUsuario(estadoJugadorDTO, principal.getName());
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
        this.salaDeEsperaService.redireccionarUsuariosAPartida(mensajeRecibidoDTO);
    }

    //PARA SPRINT 4

//    @MessageExceptionHandler({CantidadDeUsuariosInsuficientesException.class, CantidadDeUsuariosListosInsuficientesException.class})
//    @SendTo("/topic/noSePuedeIrALaPartida")
//    public MensajeRecibidoDTO enviarMensajeDeDenegacionDeAvanceAPartida(RuntimeException ex) {
//        String mensaje;
//
//        if (ex instanceof CantidadDeUsuariosInsuficientesException) {
//            mensaje = ex.getMessage();
//        } else if (ex instanceof CantidadDeUsuariosListosInsuficientesException) {
//            mensaje = ex.getMessage();
//        } else {
//            mensaje = "error inesperado";
//        }
//
//        return new MensajeRecibidoDTO(mensaje);
//    }


    @MessageMapping("/abandonarSala")
    @SendToUser("/queue/alAbandonarSala")
    public MensajeRecibidoDTO abandonarSala(MensajeDto mensajeDto, Principal principal) {
        return this.salaDeEsperaService.abandonarSala(mensajeDto,principal.getName());
    }

    @MessageExceptionHandler(Exception.class)
    public void paraExcepciones(Exception ex) {
        ex.printStackTrace();
    }


    @RequestMapping(value = "/unirseAPartidaAleatoria", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView unirseAPartidaAleatoria(HttpSession session) {
        Long idPartida = (Long) session.getAttribute("idPartida");
        Long idUsuario = (Long) session.getAttribute("idUsuario");

        if (idPartida == null || idUsuario == null) {
            return new ModelAndView("redirect:/lobby");
        }

            UsuarioPartida usuarioPartida = usuarioPartidaService.buscarUsuarioPartida(idPartida, idUsuario);
            if (usuarioPartida == null) {
                usuarioPartidaService.agregarUsuarioAPartida(idUsuario, idPartida, 0, false, Estado.EN_CURSO);
            } else {
                usuarioPartidaService.cambiarEstado(idUsuario, idPartida, Estado.EN_CURSO);
            }

            // Retornamos la vista que setea sessionStorage y redirige
            ModelAndView model = new ModelAndView("preparar-juego");
            model.addObject("idUsuario", idUsuario);
            model.addObject("idPartida", idPartida);
            return model;
    }


}
