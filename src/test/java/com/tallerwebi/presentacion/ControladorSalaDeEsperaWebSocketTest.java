package com.tallerwebi.presentacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class ControladorSalaDeEsperaWebSocketTest {

    static final String URL = "ws://localhost:8080/spring/wschat";

    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    public void queUnJugadorPuedaEstarListo() throws Exception {
        EstadoJugador estadoJugador = givenJugadorEnSala("pepe",true);
        CompletableFuture<EstadoJugador> completableFuture = whenSeleccionarBotonEstoyListo(estadoJugador);
        EstadoJugador resultado = completableFuture.get(5, TimeUnit.SECONDS);
        thenJugadorListo(resultado);
    }


    @Test
    public void queSiUnJugadorAunNoEstaListoNoSePuedaIniciarUnaPartida(){

    }

    @Test
    public void siAmbosJugadoresEstanListosSePuedaIniciarUnaPartida() {

    }


    //adicionales que pienso

    /*@Test
    public void queNoSePuedaCambiarELEstadoDelJugadorContrarioAListo(){
        ControladorSalaDeEsperaWebSocket controladorSalaDeEsperaWebSocket = new ControladorSalaDeEsperaWebSocket();
        EstadoJugador jugador1 = new EstadoJugador("jugador1", false);
        EstadoJugador jugador2 = new EstadoJugador("jugador2", false);
        jugador1.setEstaListo(true);
        assertThrows(IllegalArgumentException.class, () -> {
            controladorSalaDeEsperaWebSocket.actualizarEstadoJugador(jugador2,"jugador1");
        });
        assertTrue(jugador1.isEstaListo(), "Jugador1 debería estar listo");
        assertFalse(jugador2.isEstaListo(), "Jugador2 no debería haber cambiado su estado");
    }*/


    @Test
    public void queAlRefrescarLaPaginaNoSeReinicieWebSocket(){

    }
    private EstadoJugador givenJugadorEnSala(String nombre, boolean estaListo) {
        EstadoJugador estadoJugador = new EstadoJugador();
        estadoJugador.setJugadorId(nombre);
        estadoJugador.setEstaListo(estaListo);
        return estadoJugador;
    }

    private CompletableFuture<EstadoJugador> whenSeleccionarBotonEstoyListo(EstadoJugador estadoJugador) throws InterruptedException, ExecutionException, TimeoutException {
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
        session.send("/app/salaDeEspera", estadoJugador);
        return completableFuture;
    }

    private void thenJugadorListo(EstadoJugador resultado) {
        assertEquals("pepe", resultado.getJugadorId());
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
}

