package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.*;
import com.tallerwebi.dominio.interfaceService.AciertoService;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;

import com.tallerwebi.dominio.model.MensajeInicioRonda;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
public class WebSocketController {

    @Qualifier("partidaServiceImpl")
    private PartidaService partidaService;

    private SalaDeEsperaService salaDeEsperaService;
    private AciertoService aciertoService;


    @Autowired
    public WebSocketController(PartidaService partidaService, SalaDeEsperaService salaDeEsperaService, AciertoService aciertoService) {
        this.partidaService = partidaService;

        this.salaDeEsperaService = salaDeEsperaService;
        this.aciertoService = aciertoService;
    }



    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MensajeEnviadoDTO getMessages(MensajeRecibidoDTO mensajeRecibidoDTO, Principal principal) {
        String nombreUsuario;
        if (principal != null) {
            nombreUsuario = principal.getName();
        } else {
            nombreUsuario = "Anónimo";
        }
        return new MensajeEnviadoDTO(nombreUsuario, mensajeRecibidoDTO.getMessage());
    }


    @MessageMapping("/juego/iniciar")
    public void iniciarRonda(MensajeInicioRonda mensaje) {
        Long partidaId = mensaje.getIdPartida();






        RondaDto datosRonda = partidaService.iniciarNuevaRonda(partidaId);

    }


    @ExceptionHandler(Exception.class)
    public void handleException(Exception e) {
        e.printStackTrace();
    }

    @MessageMapping("/juego/activarComodin")
    public void activarComodin(DtoComodin dto, Principal principal) {
        String nombreUsuario = principal.getName();
        partidaService.activarComodin(dto.getIdPartida(), dto.getIdUsuario(), nombreUsuario);

    }


    @MessageMapping("/juego/intento")
    public void procesarIntento(DtoIntento intento, Principal principal){
        System.out.println("INTENTO ====== " + intento.getIntentoPalabra());

        String nombre = principal.getName();
        if (principal == null) {
            System.out.println("Principal es NULL");
            nombre = "Anónimo";
        } else {
            System.out.println("Principal name: " + principal.getName());
        }
        System.out.println("Intento recibido: " + intento.getIntentoPalabra());
        //partidaService.procesarIntento(intento, principal.getName());
        partidaService.procesarIntento(intento, nombre);
    }



    @MessageMapping("/juego/verificarAvanceDeRonda")
    public void verificarAvanceDeRonda(MensajeAvanzarRondaDTO info) {
        Long idPartida = info.getIdPartida();
        Long idRonda = info.getIdRonda();
        boolean tiempoAgotado = info.isTiempoAgotado();

        boolean todosAcertaron = aciertoService.todosAcertaron(idPartida, idRonda);

        if (todosAcertaron || tiempoAgotado) {
            DefinicionDto dto = partidaService.avanzarRonda(info);

            if (dto == null) {
                // Partida finalizada: enviar puntajes
                partidaService.enviarRankingFinal(idPartida);
            }
            // Si dto != null, ya fue enviado internamente desde el service
        }
    }

    public void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        this.partidaService.enviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);
    }
    @MessageMapping("/juego/obtenerUsuarios")
    public void obtenerUsuariosParaBloquear(DtoComodin dto, Principal principal) {
        String nombreUsuario = principal.getName();
        partidaService.obtenerUsuariosParaBloquear(dto.getIdPartida(), nombreUsuario);
    }

    @MessageMapping("/juego/bloquearUsuario")
    public void bloquearUsuario(DtoComodinBloqueo dto, Principal principal) {
        String nombreUsuario = principal.getName();
        partidaService.bloquearUsuario(dto.getIdPartida(), dto.getIdUsuario(),
                nombreUsuario, dto.getUsuarioABloquear());
    }

    @PostMapping("/abandonar")
    public void abandonar(@RequestParam Long idUsuario, @RequestParam Long idPartida, @RequestParam String nombreUsuario,HttpSession session) {
        session.removeAttribute("idPartida");
        session.removeAttribute("jugando");
        partidaService.abandonarPartida(idUsuario,idPartida,nombreUsuario);
    }

    @MessageMapping("/pedirRanking")
    public void pedirRanking(MensajeRecibidoDTO mensaje) {
        partidaService.mostrarRanking(mensaje);
    }


}
