package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.interfaceService.AciertoService;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.model.*;

import com.tallerwebi.dominio.model.MensajeEnviadoDTO;
import com.tallerwebi.dominio.model.MensajeRecibidoDTO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

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
        Long partidaId = mensaje.getPartidaId();

        RondaDto datosRonda = partidaService.iniciarNuevaRonda(partidaId);

    }


    @MessageMapping("/juego/intento")
    public void procesarIntento(DtoIntento intento, Principal principal){
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
        //si acierta pepi acerto.. sino mostrarenchat palabra prueba..
        //verificar que ronda no este terminada
        //bloquearChat
        //puntaje segun orden de acierto
        //si es correcto registro acierto en tablaAcierto y suma puntosActuales y mandarALaVista en el DTO
        //los puntosActuales de todos los usuarios
        //mandarIndividualmente a/c.usuario los puntajes y almacenarPuntajesDeTodos en un array
        //ir actualizando los putajes
        // (usar ListaUsuariosDto) y hacer topic que envie ese array
        //hacer tdd


    // == PASAR DE RONDA ==
    //verificarAciertos countDeAciertos where idRonda.getAciertos ==  "/juego/verificarAvanceDeRonda"
    //@MessageMapping("/juego/verificarAvanceDeRonda")
    //    public void finalizarRonda(DtoInfoRondaFinalizada info){
    //        partidaService.avanzarRonda(info);
    //
    //      En el servicio : template.topic/avanzarRonda Dto(datosNuevaRonda)
    //    }

    //verificar tablaacriertos
    //verificarTiempo
    //FinalizarPartida -> vistaFinal con puntajes

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


}
