package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.model.MensajeEnviado;
import com.tallerwebi.dominio.model.MensajeRecibido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class WebSocketControllerTest {

    static final String URL = "ws://localhost:8080/spring/wschat";

    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    // PARA SALA DE ESPERA
    @Test
    public void queUnJugadorPuedaEstarListo() throws Exception {
        EstadoJugador estadoJugador = givenJugadorEnSala("pepe",true);
        CompletableFuture<EstadoJugador> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/salaDeEspera",
                "/app/salaDeEspera",estadoJugador,EstadoJugador.class);
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
        WebSocketController webSocketController = new WebSocketController();
        EstadoJugador jugador1 = new EstadoJugador("jugador1", false);
        EstadoJugador jugador2 = new EstadoJugador("jugador2", false);
        jugador1.setEstaListo(true);
        assertThrows(Exception.class, () -> {
            webSocketController.actualizarEstadoJugador(jugador2,"jugador1");
        });
        assertTrue(jugador1.isEstaListo(), false);
        assertFalse(jugador2.isEstaListo(), false);
    }*/

    // PARA LA PARTIDA

    @Test
    public void siSeEstaEnUnaPartidaQueSePuedaVerLaPalabraIndicada() throws Exception {
        CompletableFuture<MensajeEnviado> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/messages",
                "/app/chat",new MensajeRecibido("nube"),MensajeEnviado.class);

        MensajeEnviado mensajeEnviado = completableFuture.get(5, TimeUnit.SECONDS);
        thenMensajeEnviadoCorrectamente("nube",mensajeEnviado);
    }


    @Test
    public void queAlRefrescarLaPaginaNoSeReinicieWebSocket(){

    }
    private void thenMensajeEnviadoCorrectamente(String mensajeEsperado, MensajeEnviado mensajeEnviado) {
        assertEquals(mensajeEsperado, mensajeEnviado.getContent());
    }
    private EstadoJugador givenJugadorEnSala(String nombre, boolean estaListo) {
        EstadoJugador estadoJugador = new EstadoJugador();
        estadoJugador.setJugadorId(nombre);
        estadoJugador.setEstaListo(estaListo);
        return estadoJugador;
    }

    private void thenJugadorListo(EstadoJugador resultado) {
        assertEquals("pepe", resultado.getJugadorId());
        assertTrue(resultado.isEstaListo());
    }

    private <T> CompletableFuture<T> whenEnvioMensajeYReciboRespuesta(
            String topic,
            String appDestination,
            Object mensajeAEnviar,
            Class<T> tipoDeRespuesta
    ) throws InterruptedException, ExecutionException, TimeoutException {

        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL, sessionHandler).get(1, TimeUnit.SECONDS);

        session.subscribe(topic, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return tipoDeRespuesta;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete(tipoDeRespuesta.cast(payload));
            }
        });

        session.send(appDestination, mensajeAEnviar);
        return completableFuture;
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

