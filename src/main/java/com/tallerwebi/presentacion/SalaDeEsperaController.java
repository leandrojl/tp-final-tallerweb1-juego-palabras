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
                }
                //}
                boolean esCreador = partidaService.verificarSiEsElCreadorDePartida(idUsuario, idPartida);

                String nombreUsuario = usuarioService.obtenerNombrePorId(idUsuario);
                model.addAttribute("idUsuario", idUsuario);
                model.addAttribute("usuario", nombreUsuario);
                model.addAttribute("idPartida", idPartida);
                model.addAttribute("esCreador", esCreador);

                return "sala-de-espera";

            }

            return "redirect:/lobby"; // Redirigir a la página de juego si la partida ya está en curso
    }


    //WEBSOCKETS EN SALA DE ESPERA

    @MessageMapping("/expulsarDeSala")
    @SendTo("/topic/jugadorExpulsado") // Se notifica a todos en este tópico
    public MensajeRecibidoDTO expulsarDeSala(MensajeDto mensajeDto, Principal principal) {

        // Obtenemos el nombre del usuario que realiza la acción (el creador)
        String nombreCreador = principal.getName();
        Long idCreador = usuarioService.obtenerUsuarioPorNombre(nombreCreador).getId();

        // Validamos si es el creador de la partida
        boolean esCreador = partidaService.verificarSiEsElCreadorDePartida(idCreador, mensajeDto.getIdPartida());

        if (esCreador) {
            // Obtenemos el NOMBRE del usuario a expulsar del campo 'message' del DTO
            String nombreUsuarioAExpulsar = mensajeDto.getNombreUsuario();

            // Verificamos que el creador no se expulse a sí mismo
            if (nombreCreador.equals(nombreUsuarioAExpulsar)) {
                throw new RuntimeException("El creador no puede expulsarse a sí mismo.");
            }

            Long idUsuarioAExpulsar = usuarioService.obtenerUsuarioPorNombre(nombreUsuarioAExpulsar).getId();

            // Cambiamos el estado del usuario en la tabla UsuarioPartida a CANCELADA
            usuarioPartidaService.cambiarEstado(idUsuarioAExpulsar, mensajeDto.getIdPartida(), Estado.CANCELADA);

            // Enviamos un mensaje a todos los clientes con el NOMBRE del usuario que fue expulsado
            return new MensajeRecibidoDTO(nombreUsuarioAExpulsar);
        }
        // Si no es el creador, lanzamos una excepción
        throw new RuntimeException("Acción no autorizada: solo el creador puede expulsar jugadores.");
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

    @MessageExceptionHandler({CantidadDeUsuariosInsuficientesException.class, CantidadDeUsuariosListosInsuficientesException.class})
    @SendTo("/topic/noSePuedeIrALaPartida")
    public MensajeRecibidoDTO enviarMensajeDeDenegacionDeAvanceAPartida(RuntimeException ex) {
        String mensaje;

        if (ex instanceof CantidadDeUsuariosInsuficientesException) {
            mensaje = ex.getMessage();
        } else if (ex instanceof CantidadDeUsuariosListosInsuficientesException) {
            mensaje = ex.getMessage();
        } else {
            mensaje = "error inesperado";
        }

        return new MensajeRecibidoDTO(mensaje);
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
