package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.model.MensajeEnviadoDTO;
import com.tallerwebi.dominio.model.MensajeRecibidoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class WebSocketController {


    private PartidaService partidaService;

    @Autowired
    public WebSocketController(PartidaService partidaService, SalaDeEsperaService salaDeEsperaService) {
        this.partidaService = partidaService;
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
    public void iniciarRonda(MensajeInicioRonda mensaje){
        Long partidaId = mensaje.getId();

        // Acá generás la ronda - INICIAR CHAT
        DefinicionDto datosRonda = partidaService.iniciarNuevaRonda(partidaId);

    }

    @MessageMapping("/juego/intento")
    @SendTo("/topic/mostrarIntento")
    public ResultadoIntentoDto procesarIntento(DtoIntento intento, Principal principal){

        return partidaService.procesarIntento(intento, principal.getName());
        //si acierta pepi acerto.. sino mostrarenchat palabra prueba..
        //verificar que ronda no este terminada
        //bloquearChat
        //puntaje segun orden de acierto
        //si es correcto registro acierto en tablaAcierto y suma puntosActuales y mandarALaVista en el DTO
        //los puntosActuales de todos los usuarios
        //mandarIndividualmente a/c.usuario los puntajes y almacenarPuntajesDeTodos en un array
        //ir actualizando los putajes
        // (usar ListaUsuariosDto) y hacer topic que envie ese array
    } //hacer tdd


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





    public void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        this.partidaService.enviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);
    }


}
