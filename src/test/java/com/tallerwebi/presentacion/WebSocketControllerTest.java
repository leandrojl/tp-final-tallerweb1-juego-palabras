
package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.RondaTimerManager;
import com.tallerwebi.dominio.DTO.*;
import com.tallerwebi.dominio.ServicioImplementacion.PartidaServiceImpl;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.RondaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebSocketControllerTest {

    static final String URL = "ws://localhost:8080/spring/wschat";
    private PartidaServiceImpl partidaService;
    private SimpMessagingTemplate messagingTemplate;
    private RondaService rondaService;
    private PartidaRepository partidaRepository;
    private RondaRepository rondaRepository;
    private UsuarioPartidaRepository usuarioPartidaRepository;
    private AciertoService aciertoService ;
    private SalaDeEsperaService salaDeEsperaService;
    private WebSocketController webSocketController;
    private WebSocketStompClient stompClient;
    private UsuarioPartidaService usuarioPartidaService;
    private RondaTimerManager rondaTimerManager;
    private GeminiBotService botService;
    private UsuarioService usuarioService;
    @BeforeEach
    public void setUp() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        rondaRepository = mock(RondaRepository.class);
        messagingTemplate = mock(SimpMessagingTemplate.class);
        rondaService = mock(RondaService.class);
        aciertoService = mock(AciertoService.class);
        usuarioPartidaService= mock(UsuarioPartidaService.class);
        partidaRepository = mock(PartidaRepository.class);
        usuarioPartidaRepository = mock(UsuarioPartidaRepository.class);
        ScheduledExecutorService timerRonda = mock(ScheduledExecutorService.class);
        rondaTimerManager = mock(RondaTimerManager.class);
        usuarioService = mock(UsuarioService.class);
        this.botService = mock(GeminiBotService.class);
        partidaService = new PartidaServiceImpl(
                messagingTemplate,
                partidaRepository,
                rondaService,
                rondaRepository,
                usuarioPartidaRepository,
                aciertoService,
                usuarioPartidaService,
                rondaTimerManager,
                botService,
                usuarioService
        );
        ReflectionTestUtils.setField(partidaService, "simpMessagingTemplate", messagingTemplate);
        salaDeEsperaService = Mockito.mock(SalaDeEsperaService.class);
        webSocketController = new WebSocketController(partidaService,salaDeEsperaService, aciertoService);
    }

    @Test
    public void siYaHayUnaPartidaIniciadaQueLosUsuariosObtenganLosDatosDeRonda() throws Exception {
        Long idPartida = 1L;
        Partida partida = new Partida("prueba", "Castellano", true, 2, 2);

        when(partidaRepository.buscarPorId(idPartida)).thenReturn(partida);

        Palabra palabra = new Palabra();
        palabra.setDescripcion("Casa");
        palabra.setDefiniciones(List.of(new Definicion("Lugar donde se vive")));

        Ronda ronda = new Ronda();
        ronda.setPalabra(palabra);
        ronda.setNumeroDeRonda(1);

        when(rondaService.crearRonda(idPartida, "Castellano")).thenReturn(ronda);

        CompletableFuture<DefinicionDto> future = new CompletableFuture<>();

        doAnswer(invocation -> {
            DefinicionDto dto = invocation.getArgument(1);
            future.complete(dto);
            return null;
        }).when(messagingTemplate).convertAndSend(eq("/topic/juego/" + idPartida), any(DefinicionDto.class));

        partidaService.iniciarNuevaRonda(idPartida);

        DefinicionDto dto = future.get(2, TimeUnit.SECONDS);

        assertNotNull(dto);
        assertEquals("Casa", dto.getPalabra());
        assertEquals("Lugar donde se vive", dto.getDefinicionTexto());
        assertEquals(1, dto.getNumeroDeRonda());
    }


    private void whenCuandoInicioLaPartida() {

    }

    private void thenPartidaLista(DefinicionDto retorno) {
        assertNotNull(retorno.getPalabra());
        assertNotNull(retorno.getDefinicionTexto());
        assertThat(retorno.getNumeroDeRonda(), equalTo(1L));

    }



    private void givenInicializarDatos() {

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



}

