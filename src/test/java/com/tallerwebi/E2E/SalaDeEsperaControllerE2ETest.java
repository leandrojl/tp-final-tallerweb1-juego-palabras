package com.tallerwebi.E2E;

import com.tallerwebi.dominio.DTO.EstadoJugadorDTO;
import com.tallerwebi.dominio.DTO.ListaUsuariosDTO;
import com.tallerwebi.dominio.DTO.MensajeRecibidoDTO;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.presentacion.SalaDeEsperaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SalaDeEsperaControllerE2ETest {

    static final String URL = "ws://localhost:8080/spring/wschat";
    private SalaDeEsperaService salaDeEsperaService;
    private UsuarioPartidaService usuarioPartidaService;
    private SalaDeEsperaController salaDeEsperaController;
    private UsuarioService usuarioService;
    private PartidaService partidaService;
    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setUp() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        salaDeEsperaService = Mockito.mock(SalaDeEsperaService.class);
        usuarioPartidaService = Mockito.mock(UsuarioPartidaService.class);
        usuarioService = Mockito.mock(UsuarioService.class);
        partidaService = Mockito.mock(PartidaService.class);
        salaDeEsperaController = new SalaDeEsperaController(
                salaDeEsperaService,
                usuarioService,
                usuarioPartidaService,
                partidaService);
    }

    @Test
    public void queUnJugadorPuedaEstarListo() throws Exception {
        Long idPartida = 1L;
        EstadoJugadorDTO estadoJugadorDTO = givenJugadorEnSala(idPartida,"pepe",true);
        CompletableFuture<EstadoJugadorDTO> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/salaDeEspera" +
                        "/" + idPartida,
                "/app/salaDeEspera", estadoJugadorDTO,"pepe", EstadoJugadorDTO.class);
        EstadoJugadorDTO resultado = completableFuture.get(5, TimeUnit.SECONDS);
        thenJugadorListo(resultado);
    }


    @Test
    public void queNoSePuedaCambiarELEstadoDelJugadorContrarioAListo() throws Exception {
        EstadoJugadorDTO jugador2 = new EstadoJugadorDTO(1L,"jugador2", true);

        CompletableFuture<MensajeRecibidoDTO> errorEsperado = whenEnvioYReciboError(
                "/app/salaDeEspera",
                jugador2,
                "jugador1"
        );

        MensajeRecibidoDTO errorMensaje = errorEsperado.get(5, TimeUnit.SECONDS);
        assertEquals("Error, no se puede alterar el estado de otro jugador", errorMensaje.getMessage());
    }


    @Test
    public void siAlguienSeUneALaSalaDeEsperaLosDemasJugadoresPuedenVerlo() throws Exception {
        Long idPartida = 1L;
        String nombreUsuarioQueAcabaDeUnirseALaSala = "jose";
        CompletableFuture<MensajeRecibidoDTO> usuarioYaEnSalaDeEspera = givenUsuarioConectado("pepe","/topic" +
                "/cuandoUsuarioSeUneASalaDeEspera/" + idPartida,false,null , MensajeRecibidoDTO.class);
        givenUsuarioConectado(nombreUsuarioQueAcabaDeUnirseALaSala,"/topic/cuandoUsuarioSeUneASalaDeEspera/" + idPartida,
                true,null,
                MensajeRecibidoDTO.class);

        MensajeRecibidoDTO mensajeRecibidoDTO = usuarioYaEnSalaDeEspera.get(5, TimeUnit.SECONDS);
        assertEquals(nombreUsuarioQueAcabaDeUnirseALaSala, mensajeRecibidoDTO.getMessage());
    }

    @Test
    public void siYaHayUsuariosEnLaSalaQueAquelNuevoUsuarioQueSeUnePuedaVerLosQueYaEstanEnDichaSala() throws Exception {
        givenUsuarioConectado("pepe","/topic" +
                "/cuandoUsuarioSeUneASalaDeEspera",true , null,MensajeRecibidoDTO.class);
        CompletableFuture<ListaUsuariosDTO> usuarioQueAcabaDeUnirseALaSala =
                givenUsuarioConectado("jose","/user/queue/jugadoresExistentes",
                        true, null,
                        ListaUsuariosDTO.class);
        ListaUsuariosDTO lista = usuarioQueAcabaDeUnirseALaSala.get(2, TimeUnit.SECONDS);
        assertTrue(lista.getUsuarios().contains("pepe"));

    }


    @Test
    public void queSePuedaIniciarLaPartida() throws Exception {
        Long idPartida = 1L;
        MensajeRecibidoDTO mensajeParaIniciarPartida = new MensajeRecibidoDTO("mensaje de inicio de partida",idPartida);
        CompletableFuture<MensajeRecibidoDTO> elQueInicioLaPartida = givenUsuarioConectado("pepe","/user/queue" +
                "/irAPartida",true,mensajeParaIniciarPartida , MensajeRecibidoDTO.class);
        MensajeRecibidoDTO mensaje = elQueInicioLaPartida.get(2, TimeUnit.SECONDS);
        thenIniciarLaPartida(mensaje,"http://localhost:8080/spring/lobby");
    }
    //PARA EL SPRINT 4


    @Test
    public void siAlTratarDeIniciarLaPartidaNoSeCumpleConElRequisitoDeUsuariosMinimosQueSeLesEnvieUnMensajeDeDenegacion() throws Exception {
        Long idPartida = 1L;
        MensajeRecibidoDTO mensajeParaIniciarPartida = new MensajeRecibidoDTO("mensaje de inicio de partida",idPartida);

        when(salaDeEsperaService.redireccionarUsuariosAPartida(mensajeParaIniciarPartida)).thenReturn(false);

        CompletableFuture<MensajeRecibidoDTO> elQueInicioLaPartida = givenUsuarioConectado("pepe","/topic/noSePuedeIrALaPartida"
                ,false,mensajeParaIniciarPartida , MensajeRecibidoDTO.class);
        MensajeRecibidoDTO mensaje = elQueInicioLaPartida.get(2, TimeUnit.SECONDS);
        thenIniciarLaPartida(mensaje,    "Cantidad insuficiente de usuarios para iniciar partida");
    }















    private void thenIniciarLaPartida(MensajeRecibidoDTO mensajeDelServidor, String mensajeEsperado) {
        assertEquals(mensajeEsperado, mensajeDelServidor.getMessage());
    }


    private <T> CompletableFuture<T> givenUsuarioConectado(
            String nombreUsuario,
            String dondeSeSuscribe,
            Boolean notificaALosDemasUsuarioQueSeUneALaSala,
            MensajeRecibidoDTO mensajeParaIniciarPartida,
            Class<T> tipoDeRespuesta
    ) throws Exception {

        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {};
        StompSession session = stompClient.connect(URL + "?usuario=" + nombreUsuario, sessionHandler)
                .get(1, TimeUnit.SECONDS);

        session.subscribe(dondeSeSuscribe, new StompFrameHandler() {
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
            session.send("/app/usuarioSeUneASalaDeEspera",new MensajeRecibidoDTO(nombreUsuario,1L));
        }
        if(mensajeParaIniciarPartida != null){
            session.send("/app/inicioPartida",mensajeParaIniciarPartida);
        }

        return completableFuture;
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

        session.subscribe("/user/queue/mensajeAlIntentarCambiarEstadoDeOtroJugador", new StompFrameHandler() {

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

    private EstadoJugadorDTO givenJugadorEnSala(Long idPartida, String nombre, boolean estaListo) {
        EstadoJugadorDTO estadoJugadorDTO = new EstadoJugadorDTO();
        estadoJugadorDTO.setUsername(nombre);
        estadoJugadorDTO.setEstaListo(estaListo);
        estadoJugadorDTO.setIdPartida(idPartida);
        return estadoJugadorDTO;
    }

    private void thenJugadorListo(EstadoJugadorDTO resultado) {
        assertEquals("pepe", resultado.getUsername());
        assertTrue(resultado.isEstaListo());
    }
}
