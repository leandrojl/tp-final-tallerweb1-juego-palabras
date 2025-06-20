package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.PartidaService;
import com.tallerwebi.dominio.PuntajeService;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WebSocketGameController {

    private final PartidaService partidaService;
    private final PuntajeService puntajeService;

    @Autowired
    public WebSocketGameController(PartidaService partidaService, PuntajeService puntajeService) {
        this.partidaService = partidaService;
        this.puntajeService = puntajeService;
    }

    // ===== MANEJO DE SALA DE ESPERA =====
    @MessageMapping("/salaDeEspera")
    @SendTo("/topic/salaDeEspera")
    public EstadoJugador actualizarEstadoJugador(EstadoJugador estadoJugador, Principal principal) {
        String nombreUsuario = estadoJugador.getUsername();
        if (!nombreUsuario.equals(principal.getName())) {
            throw new UsuarioInvalidoException("Error, no se puede alterar el estado de otro jugador");
        }
        return estadoJugador;
    }

    // ===== UNIRSE A PARTIDA =====
    @MessageMapping("/partida/unirse")
    @SendTo("/topic/partida/estado")
    public EstadoPartida unirseAPartida(UnirsePartidaRequest request, Principal principal) {
        String jugadorId = principal.getName();
        Jugador jugador = new Jugador(jugadorId, request.getNombreJugador(), "", "");

        boolean exito = partidaService.unirseAPartida(request.getPartidaId(), jugadorId, jugador);

        if (exito) {
            return partidaService.obtenerEstadoPartida(request.getPartidaId());
        } else {
            throw new RuntimeException("No se pudo unir a la partida");
        }
    }

    // ===== INICIAR PARTIDA =====
    @MessageMapping("/partida/iniciar")
    @SendTo("/topic/partida/iniciada")
    public EstadoPartida iniciarPartida(IniciarPartidaRequest request, Principal principal) {
        boolean exito = partidaService.iniciarPartida(request.getPartidaId());

        if (exito) {
            return partidaService.obtenerEstadoPartida(request.getPartidaId());
        } else {
            throw new RuntimeException("No se pudo iniciar la partida");
        }
    }

    // ===== PROCESAR INTENTO =====
    @MessageMapping("/juego/intentar")
    @SendTo("/topic/juego/resultado")
    public ResultadoJuego procesarIntento(IntentoRequest request, Principal principal) {
        String jugadorId = principal.getName();

        ResultadoIntento resultado = partidaService.procesarIntento(
                request.getPartidaId(),
                jugadorId,
                request.getIntento(),
                request.getTiempoRestante()
        );

        // Obtener puntajes actualizados
        Map<String, Integer> puntajes = new HashMap<>();
        EstadoPartida estadoPartida = partidaService.obtenerEstadoPartida(request.getPartidaId());

        for (String jId : estadoPartida.getJugadores()) {
            puntajes.put(jId, puntajeService.obtenerPuntaje(jId));
        }

        ResultadoJuego resultadoJuego = new ResultadoJuego();
        resultadoJuego.setJugadorId(jugadorId);
        resultadoJuego.setCorrecto(resultado.isCorrecto());
        resultadoJuego.setPuntosObtenidos(resultado.getPuntosObtenidos());
        resultadoJuego.setPalabra(resultado.getPalabra());
        resultadoJuego.setNuevaRonda(resultado.isNuevaRonda());
        resultadoJuego.setPartidaTerminada(resultado.isPartidaTerminada());
        resultadoJuego.setNuevaPalabra(resultado.getNuevaPalabra());
        resultadoJuego.setNuevaDefinicion(resultado.getNuevaDefinicion());
        resultadoJuego.setPuntajes(puntajes);

        if (resultado.isPartidaTerminada()) {
            // Calcular ranking final
            List<Map.Entry<String, Integer>> ranking = puntajes.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .collect(Collectors.toList());
            resultadoJuego.setRankingFinal(ranking);
        }

        return resultadoJuego;
    }

    // ===== FIN DE RONDA POR TIEMPO =====
    @MessageMapping("/juego/finRonda")
    @SendTo("/topic/juego/nuevaRonda")
    public EstadoPartida finalizarRondaPorTiempo(FinRondaRequest request, Principal principal) {
        partidaService.finalizarRondaPorTiempo(request.getPartidaId());
        return partidaService.obtenerEstadoPartida(request.getPartidaId());
    }

    // ===== CHAT DEL JUEGO =====
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public MensajeEnviado getMessages(MensajeRecibido mensajeRecibido, Principal principal) {
        String nombreUsuario = (principal != null) ? principal.getName() : "Anónimo";
        return new MensajeEnviado(nombreUsuario, mensajeRecibido.getMessage());
    }

    // ===== MANEJO DE EXCEPCIONES =====
    @MessageExceptionHandler(UsuarioInvalidoException.class)
    @SendToUser("/queue/errors")
    public MensajeRecibido handleUsuarioInvalidoException(UsuarioInvalidoException ex) {
        return new MensajeRecibido(ex.getMessage());
    }

    @MessageExceptionHandler(RuntimeException.class)
    @SendToUser("/queue/errors")
    public MensajeRecibido handleRuntimeException(RuntimeException ex) {
        return new MensajeRecibido("Error: " + ex.getMessage());
    }
}