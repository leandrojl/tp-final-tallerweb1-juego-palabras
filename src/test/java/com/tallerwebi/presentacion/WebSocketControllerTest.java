package com.tallerwebi.presentacion;
/*
import com.tallerwebi.dominio.PartidaService;
import com.tallerwebi.dominio.model.EstadoJugador;
import com.tallerwebi.dominio.model.MensajeEnviado;
import com.tallerwebi.dominio.model.MensajeRecibido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import com.tallerwebi.presentacion.WebSocketGameController;
import java.lang.reflect.Type;
import java.util.concurrent.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebSocketControllerTest {

    static final String URL = "ws://localhost:8080/spring/wschat";

    private WebSocketStompClient stompClient;
    private PartidaService partidaService;
    private WebSocketGameController webSocketController;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        partidaService = Mockito.mock(PartidaService.class);
        webSocketController = new WebSocketGameController(partidaService);
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
    public void queNoSePuedaCambiarELEstadoDelJugadorContrarioAListo() throws Exception {  //EN DESARROLLO
        EstadoJugador jugador2 = new EstadoJugador("jugador2", true);

        CompletableFuture<MensajeRecibido> errorEsperado = whenEnvioYReciboError(
                "/app/salaDeEspera",
                jugador2,
                "jugador1"
        );

        MensajeRecibido errorMensaje = errorEsperado.get(5, TimeUnit.SECONDS);
        assertEquals("Error, no se puede alterar el estado de otro jugador", errorMensaje.getMessage());
    }

    @Test
    public void dadoQueHayDosUsuariosConectadosAlWebSocketsQueSePuedaEnviarUnMensajePrivadoAUnUsuario() throws Exception {
        CompletableFuture<MensajeEnviado> futurePepe = givenUsuarioConectado("pepe");
        CompletableFuture<MensajeEnviado> futureJose = givenUsuarioConectado("jose");
        String mensaje = "Este mensaje le deberia llegar a pepe";
        String nombreUsuario = "pepe";

        doAnswer(invocation -> {
            String usuario = invocation.getArgument(0);
            String msg = invocation.getArgument(1);

            if (usuario.equals("pepe")) {
                futurePepe.complete(new MensajeEnviado(usuario, msg));
            } else if (usuario.equals("jose")) {
                futureJose.complete(new MensajeEnviado(usuario, msg));
            }
            return true;
        }).when(partidaService).enviarMensajeAUsuarioEspecifico(anyString(), anyString());

        whenEnviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);

        assertThrows(TimeoutException.class, () -> futureJose.get(5, TimeUnit.SECONDS));
        MensajeEnviado mensajeEnviado = futurePepe.get(5, TimeUnit.SECONDS);
        assertEquals(mensaje,mensajeEnviado.getContent());
        assertEquals(nombreUsuario,mensajeEnviado.getUsername());
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
    private void whenEnviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        this.webSocketController.enviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);
    }
    private CompletableFuture<MensajeEnviado> givenUsuarioConectado(String nombreUsuario) throws Exception{
        CompletableFuture <MensajeEnviado> completableFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL + "?usuario=" + nombreUsuario, sessionHandler)
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/user/queue/paraTest", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {

                return MensajeEnviado.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((MensajeEnviado) payload);
            }
        });
        return completableFuture;
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

    private CompletableFuture<MensajeRecibido> whenEnvioYReciboError(
            String appDestination,
            Object mensajeAEnviar,
            String nombreEmisor
    ) throws Exception {
        CompletableFuture<MensajeRecibido> errorFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL + "?usuario=" + nombreEmisor, sessionHandler)
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/user/queue/errors", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {

                return MensajeRecibido.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorFuture.complete((MensajeRecibido) payload);
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
*/
