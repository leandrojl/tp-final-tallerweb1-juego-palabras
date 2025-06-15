package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.model.EstadoJugador;
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

    @Test
    public void queUnJugadorPuedaEstarListo() throws Exception {
        EstadoJugador estadoJugador = givenJugadorEnSala("pepe",true);
        CompletableFuture<EstadoJugador> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/salaDeEspera",
                "/app/salaDeEspera",estadoJugador,"pepe",EstadoJugador.class);
        EstadoJugador resultado = completableFuture.get(5, TimeUnit.SECONDS);
        thenJugadorListo(resultado);
    }


    @Test
    public void queNoSePuedaCambiarELEstadoDelJugadorContrarioAListo() throws Exception {  //EN DESARROLLO
        EstadoJugador jugador2 = new EstadoJugador("jugador2", true);

        CompletableFuture<String> errorEsperado = whenEnvioYReciboError(
                "/app/salaDeEspera",
                jugador2,
                "jugador1"
        );

        String errorMensaje = errorEsperado.get(5, TimeUnit.SECONDS);
        assertEquals("No se puede modificar el estado de otro jugador", errorMensaje);
    }


    @Test
    public void siSeEstaEnUnaPartidaQueSePuedaVerLaPalabraIndicada() throws Exception {
        CompletableFuture<MensajeEnviado> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/messages",
                "/app/chat",new MensajeRecibido("nube"),"pepe",MensajeEnviado.class);

        MensajeEnviado mensajeEnviado = completableFuture.get(5, TimeUnit.SECONDS);
        thenMensajeEnviadoCorrectamente("nube",mensajeEnviado);
    }

    @Test
    public void siSeEstaEnUnaPartidaQueSePuedaSaberQuienEscribioLaPlabraIndicada() throws Exception{


        CompletableFuture<MensajeEnviado> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/messages",
                "/app/chat",new MensajeRecibido("nube"),"pepe",MensajeEnviado.class);

        MensajeEnviado mensajeEnviado = completableFuture.get(5, TimeUnit.SECONDS);
        thenMensajeEnviadoCorrectamente("nube",mensajeEnviado);
        thenSeVeQuienEscribioElMensaje("pepe",mensajeEnviado.getUsername());
    }



    @Test
    public void queSiUnJugadorAunNoEstaListoNoSePuedaIniciarUnaPartida(){

    }

    @Test
    public void siAmbosJugadoresEstanListosSePuedaIniciarUnaPartida() {

    }

    @Test
    public void siSeEstaEnUnaPartidaYNoSeSabeQuienEscribioLaPalabraQueDeError() {
    }

    @Test
    public void queAlRefrescarLaPaginaNoSeReinicieWebSocket(){

    }

    private void thenSeVeQuienEscribioElMensaje(String nombreEsperado, String username) {
        assertEquals(nombreEsperado,username);
    }
    private void thenMensajeEnviadoCorrectamente(String mensajeEsperado, MensajeEnviado mensajeEnviado) {
        assertEquals(mensajeEsperado, mensajeEnviado.getContent());
    }
    private EstadoJugador givenJugadorEnSala(String nombre, boolean estaListo) {
        EstadoJugador estadoJugador = new EstadoJugador();
        estadoJugador.setUsername(nombre);
        estadoJugador.setEstaListo(estaListo);
        return estadoJugador;
    }

    private void thenJugadorListo(EstadoJugador resultado) {
        assertEquals("pepe", resultado.getUsername());
        assertTrue(resultado.isEstaListo());
    }

    private CompletableFuture<String> whenEnvioYReciboError(
            String appDestination,
            Object mensajeAEnviar,
            String nombreEmisor
    ) throws Exception {
        CompletableFuture<String> errorFuture = new CompletableFuture<>();
        System.out.println(URL + "?usuario=" + nombreEmisor);
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL + "?usuario=" + nombreEmisor, sessionHandler)
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/user/queue/errors", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {

                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorFuture.complete((String) payload);
            }
        });

        session.send(appDestination, mensajeAEnviar);

        return errorFuture;
    }


    private <T> CompletableFuture<T> whenEnvioMensajeYReciboRespuesta(
            String topic,
            String appDestination,
            Object mensajeAEnviar,
            String nombreEmisor,
            Class<T> tipoDeRespuesta
    ) throws InterruptedException, ExecutionException, TimeoutException {

        CompletableFuture<T> completableFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        String urlConUsuario = URL + "?usuario=" + nombreEmisor;
        StompSession session = stompClient.connect(urlConUsuario, sessionHandler).get(1, TimeUnit.SECONDS);


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
}

