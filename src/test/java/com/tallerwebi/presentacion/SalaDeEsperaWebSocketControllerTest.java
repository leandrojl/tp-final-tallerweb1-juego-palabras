package com.tallerwebi.presentacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class SalaDeEsperaWebSocketControllerTest {

    static final String URL = "ws://localhost:8080/spring/wschat";

    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void queUnJugadorPuedaEstarListo() throws Exception {
        CompletableFuture<EstadoJugador> completableFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL, sessionHandler).get(1, TimeUnit.SECONDS);
        session.subscribe("/topic/salaDeEspera", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return EstadoJugador.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((EstadoJugador) payload);
            }
        });
        EstadoJugador input = new EstadoJugador();
        input.setJugadorId("jugador123");
        input.setEstaListo(true);
        session.send("/app/salaDeEspera", input);
        EstadoJugador resultado = completableFuture.get(5, TimeUnit.SECONDS);
        thenJugadorListo(resultado);
    }

    private static void thenJugadorListo(EstadoJugador resultado) {
        assertEquals("jugador123", resultado.getJugadorId());
        assertTrue(resultado.isEstaListo());
    }

    public static class EstadoJugador {
        private String jugadorId;
        private boolean estaListo;

        public String getJugadorId() {
            return jugadorId;
        }

        public void setJugadorId(String jugadorId) {
            this.jugadorId = jugadorId;
        }

        public boolean isEstaListo() {
            return estaListo;
        }

        public void setEstaListo(boolean estaListo) {
            this.estaListo = estaListo;
        }
    }

    @Test
    public void queSiUnJugadorAunNoEstaListoNoSePuedaIniciarUnaPartida(){

    }

    @Test
    public void siAmbosJugadoresEstanListosSePuedaIniciarUnaPartida() {

    }


    //adicionales que pienso

    @Test
    public void queNoSePuedaCambiarELEstadoDelJugadorContrarioAListo(){

    }


    @Test
    public void queAlRefrescarLaPaginaNoSeReinicieWebSocket(){

    }
}

