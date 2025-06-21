package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.Partida2Service;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.model.EstadoJugadorDTO;
import com.tallerwebi.dominio.model.ListaUsuariosDTO;
import com.tallerwebi.dominio.model.MensajeEnviadoDTO;
import com.tallerwebi.dominio.model.MensajeRecibidoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebSocketControllerTest {

    static final String URL = "ws://localhost:8080/spring/wschat";

    private WebSocketStompClient stompClient;
    private Partida2Service partidaService;
    private SalaDeEsperaService salaDeEsperaService;
    private WebSocketController webSocketController;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        partidaService = Mockito.mock(Partida2Service.class);
        salaDeEsperaService = Mockito.mock(SalaDeEsperaService.class);
        webSocketController = new WebSocketController(partidaService,salaDeEsperaService);
    }

    @Test
    public void queUnJugadorPuedaEstarListo() throws Exception {
        EstadoJugadorDTO estadoJugadorDTO = givenJugadorEnSala("pepe",true);
        CompletableFuture<EstadoJugadorDTO> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/salaDeEspera",
                "/app/salaDeEspera", estadoJugadorDTO,"pepe", EstadoJugadorDTO.class);
        EstadoJugadorDTO resultado = completableFuture.get(5, TimeUnit.SECONDS);
        thenJugadorListo(resultado);
    }




    @Test
    public void siSeEstaEnUnaPartidaQueSePuedaVerLaPalabraIndicada() throws Exception {
        CompletableFuture<MensajeEnviadoDTO> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/messages",
                "/app/chat",new MensajeRecibidoDTO("nube"),"pepe", MensajeEnviadoDTO.class);

        MensajeEnviadoDTO mensajeEnviadoDTO = completableFuture.get(5, TimeUnit.SECONDS);
        thenMensajeEnviadoCorrectamente("nube", mensajeEnviadoDTO);
    }

    @Test
    public void siSeEstaEnUnaPartidaQueSePuedaSaberQuienEscribioLaPlabraIndicada() throws Exception{


        CompletableFuture<MensajeEnviadoDTO> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/messages",
                "/app/chat",new MensajeRecibidoDTO("nube"),"pepe", MensajeEnviadoDTO.class);

        MensajeEnviadoDTO mensajeEnviadoDTO = completableFuture.get(5, TimeUnit.SECONDS);
        thenMensajeEnviadoCorrectamente("nube", mensajeEnviadoDTO);
        thenSeVeQuienEscribioElMensaje("pepe", mensajeEnviadoDTO.getUsername());
    }

    //PARA SPRINT 3 MIO

    @Test
    public void queNoSePuedaCambiarELEstadoDelJugadorContrarioAListo() throws Exception {
        EstadoJugadorDTO jugador2 = new EstadoJugadorDTO("jugador2", true);

        CompletableFuture<MensajeRecibidoDTO> errorEsperado = whenEnvioYReciboError(
                "/app/salaDeEspera",
                jugador2,
                "jugador1"
        );

        MensajeRecibidoDTO errorMensaje = errorEsperado.get(5, TimeUnit.SECONDS);
        assertEquals("Error, no se puede alterar el estado de otro jugador", errorMensaje.getMessage());
    }

    @Test
    public void dadoQueHayDosUsuariosConectadosAlWebSocketsQueSePuedaEnviarUnMensajePrivadoAUnUsuario() throws Exception {
        CompletableFuture<MensajeEnviadoDTO> futurePepe = givenUsuarioConectado("pepe","/user/queue/paraTest", false,
                MensajeEnviadoDTO.class);
        CompletableFuture<MensajeEnviadoDTO> futureJose = givenUsuarioConectado("jose","/user/queue/paraTest", false,
                MensajeEnviadoDTO.class);
        String mensaje = "Este mensaje le deberia llegar a pepe";
        String nombreUsuario = "pepe";

        doAnswer(invocation -> {
            String usuario = invocation.getArgument(0);
            String msg = invocation.getArgument(1);

            if (usuario.equals("pepe")) {
                futurePepe.complete(new MensajeEnviadoDTO(usuario, msg));
            } else if (usuario.equals("jose")) {
                futureJose.complete(new MensajeEnviadoDTO(usuario, msg));
            }
            return true;
        }).when(partidaService).enviarMensajeAUsuarioEspecifico(anyString(), anyString());

        whenEnviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);

        assertThrows(TimeoutException.class, () -> futureJose.get(5, TimeUnit.SECONDS));
        MensajeEnviadoDTO mensajeEnviadoDTO = futurePepe.get(5, TimeUnit.SECONDS);
        assertEquals(mensaje, mensajeEnviadoDTO.getContent());
        assertEquals(nombreUsuario, mensajeEnviadoDTO.getUsername());
    }

    @Test
    public void siAlguienSeUneALaSalaDeEsperaLosDemasJugadoresPuedenVerlo() throws Exception {
        String nombreUsuarioQueAcabaDeUnirseALaSala = "jose";
        CompletableFuture<MensajeRecibidoDTO> usuarioYaEnSalaDeEspera = givenUsuarioConectado("pepe","/topic" +
                "/cuandoUsuarioSeUneASalaDeEspera",false , MensajeRecibidoDTO.class);
        givenUsuarioConectado(nombreUsuarioQueAcabaDeUnirseALaSala,"/topic/cuandoUsuarioSeUneASalaDeEspera",
                        true,
                        MensajeRecibidoDTO.class);

        MensajeRecibidoDTO mensajeRecibidoDTO = usuarioYaEnSalaDeEspera.get(5, TimeUnit.SECONDS);
        assertEquals(nombreUsuarioQueAcabaDeUnirseALaSala, mensajeRecibidoDTO.getMessage());
    }

    @Test
    public void siYaHayUsuariosEnLaSalaQueAquelNuevoUsuarioQueSeUnePuedaVerLosQueYaEstanEnDichaSala() throws Exception {
        givenUsuarioConectado("pepe","/topic" +
                "/cuandoUsuarioSeUneASalaDeEspera",true , MensajeRecibidoDTO.class);
        CompletableFuture<ListaUsuariosDTO> usuarioQueAcabaDeUnirseALaSala =
                givenUsuarioConectado("jose","/user/queue/jugadoresExistentes",
                        true,
                        ListaUsuariosDTO.class);
        ListaUsuariosDTO lista = usuarioQueAcabaDeUnirseALaSala.get(2, TimeUnit.SECONDS);
        assertTrue(lista.getUsuarios().contains("pepe"));

    }
    private <T> CompletableFuture<T> givenUsuarioConectado(
            String nombreUsuario,
            String dondeSeConecta,
            Boolean notificaALosDemasUsuarioQueSeUneALaSala,
            Class<T> tipoDeRespuesta
    ) throws Exception {

        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL + "?usuario=" + nombreUsuario, sessionHandler)
                .get(1, TimeUnit.SECONDS);

        session.subscribe(dondeSeConecta, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return tipoDeRespuesta;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete(tipoDeRespuesta.cast(payload));
            }
        });

        if(notificaALosDemasUsuarioQueSeUneALaSala){
            session.send("/app/usuarioSeUneASalaDeEspera",new MensajeRecibidoDTO(nombreUsuario));
        }

        return completableFuture;
    }

    private void whenEnviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        this.webSocketController.enviarMensajeAUsuarioEspecifico(nombreUsuario,mensaje);
    }


    private void thenSeVeQuienEscribioElMensaje(String nombreEsperado, String username) {
        assertEquals(nombreEsperado,username);
    }
    private void thenMensajeEnviadoCorrectamente(String mensajeEsperado, MensajeEnviadoDTO mensajeEnviadoDTO) {
        assertEquals(mensajeEsperado, mensajeEnviadoDTO.getContent());
    }
    private EstadoJugadorDTO givenJugadorEnSala(String nombre, boolean estaListo) {
        EstadoJugadorDTO estadoJugadorDTO = new EstadoJugadorDTO();
        estadoJugadorDTO.setUsername(nombre);
        estadoJugadorDTO.setEstaListo(estaListo);
        return estadoJugadorDTO;
    }

    private void thenJugadorListo(EstadoJugadorDTO resultado) {
        assertEquals("pepe", resultado.getUsername());
        assertTrue(resultado.isEstaListo());
    }

    private CompletableFuture<MensajeRecibidoDTO> whenEnvioYReciboError(
            String appDestination,
            Object mensajeAEnviar,
            String nombreEmisor
    ) throws Exception {
        CompletableFuture<MensajeRecibidoDTO> errorFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL + "?usuario=" + nombreEmisor, sessionHandler)
                .get(1, TimeUnit.SECONDS);

        session.subscribe("/user/queue/errors", new StompFrameHandler() {

            @Override
            public Type getPayloadType(StompHeaders headers) {

                return MensajeRecibidoDTO.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                errorFuture.complete((MensajeRecibidoDTO) payload);
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

