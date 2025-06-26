package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.PartidaServiceImpl;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.RondaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SalaDeEsperaControllerTest {

    static final String URL = "ws://localhost:8080/spring/wschat";
    private SalaDeEsperaService salaDeEsperaService;
    private SalaDeEsperaController salaDeEsperaController;
    private UsuarioService usuarioService;
    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setUp() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        salaDeEsperaService = Mockito.mock(SalaDeEsperaService.class);
        salaDeEsperaController = new SalaDeEsperaController(salaDeEsperaService,usuarioService);
    }


    @Test
    public void cuandoAgregoUnJugadorALaSalaDeEsperaLoRedirijaALaSalaDeEspera() {

        Jugador jugador = dadoQueTengoUnJugadorConNombre("Messi");

        ModelAndView mavJugador = cuandoAgregoAUnJugadorALaSalaDeEspera(jugador);

        entoncesRedirigoAlJugadorALaSalaDeEspera(mavJugador);

    }

    @Test
    public void dadoQueTengoUnaSalaDeEspera(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        assertThat(salaDeEspera, notNullValue());
    }

    @Test
    public void dadoQueTengoUnaSalaDeEsperaConDosJugadores(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        Usuario jugador1 = new Usuario("Messi");
        Usuario jugador2 = new Usuario("Cristiano");

        salaDeEspera.setJugador1(jugador1);
        salaDeEspera.setJugador2(jugador2);

        assertThat(salaDeEspera.getJugador1(), equalTo(jugador1));
        assertThat(salaDeEspera.getJugador2(), equalTo(jugador2));
    }



    @Test
    public void dadoQueTengoUnaSalaDeEsperaConDosJugadoresQueEstanListos(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        Usuario jugador1 = new Usuario("Messi");
        Usuario jugador2 = new Usuario("Cristiano");

        salaDeEspera.setJugador1(jugador1);
        salaDeEspera.setJugador2(jugador2);

        salaDeEspera.setJugador1Listo(true);
        salaDeEspera.setJugador2Listo(true);

        assertThat(salaDeEspera.getJugador1Listo(), equalTo(true));
        assertThat(salaDeEspera.getJugador2Listo(), equalTo(true));

    }

    @Test
    public void dadoQueTengoUnaSalaDeEsperaVaciaQueNoSePuedanPonerListoLosJugadores(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();
        //dado que no hay jugadores en la sala de espera
        dadoQueNoTengoJugadoresEnLaSalaDeEspera();

        //cuando intento poner a los jugadores listos
        salaDeEspera.setJugador1Listo(true);
        salaDeEspera.setJugador2Listo(true);

        //entonces no se puede poner a los jugadores listos
        assertThat(salaDeEspera.getJugador1Listo(), equalTo(false));
        assertThat(salaDeEspera.getJugador2Listo(), equalTo(false));

    }

    @Test
    public void dadoQueTengoUnaSalaDeEsperaConUnJugadorQueNoEstaListo(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        Usuario jugador1 = new Usuario("Messi");

        salaDeEspera.setJugador1(jugador1);

        salaDeEspera.setJugador1Listo(false);

        assertThat(salaDeEspera.getJugador1Listo(), equalTo(false));
    }

    @Test
    public void dadoQueTengoUnaSalaDeEsperaConUnJugadorQueEstaListo(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        Usuario jugador1 = new Usuario("Messi");

        salaDeEspera.setJugador1(jugador1);
        salaDeEspera.setJugador1Listo(true);

        assertThat(salaDeEspera.getJugador1Listo(), equalTo(true));
    }

    @Test
    public void dadoQueTengoUnaSalaDeEsperaConUnJugadorQueNoEstaListoYOtroQueSi(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        Usuario jugador1 = new Usuario("Messi");
        Usuario jugador2 = new Usuario("Cristiano");

        salaDeEspera.setJugador1(jugador1);
        salaDeEspera.setJugador2(jugador2);

        salaDeEspera.setJugador1Listo(false);
        salaDeEspera.setJugador2Listo(true);

        assertThat(salaDeEspera.getJugador1Listo(), equalTo(false));
        assertThat(salaDeEspera.getJugador2Listo(), equalTo(true));
    }

    @Test
    public void dadoQueTengoUnaSalaDeEsperaConUnJugadorQueNoEstaListoYOtroQueSiQueNoSePuedaIniciarPartida(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        Usuario jugador1 = new Usuario("Messi");
        Usuario jugador2 = new Usuario("Cristiano");

        salaDeEspera.setJugador1(jugador1);
        salaDeEspera.setJugador2(jugador2);

        salaDeEspera.setJugador1Listo(false);
        salaDeEspera.setJugador2Listo(true);

        assertThat(salaDeEspera.estanAmbosListos(), equalTo(false));
    }


    @Test
    public void siNoHayUnJugadorQueRetorneElControladorAlLobby(){

    }



    // TESTS CON WEBSOCKETS PARA LA SALA DE ESPERA


    @Test
    public void queUnJugadorPuedaEstarListo() throws Exception {
        EstadoJugadorDTO estadoJugadorDTO = givenJugadorEnSala("pepe",true);
        CompletableFuture<EstadoJugadorDTO> completableFuture = whenEnvioMensajeYReciboRespuesta("/topic/salaDeEspera",
                "/app/salaDeEspera", estadoJugadorDTO,"pepe", EstadoJugadorDTO.class);
        EstadoJugadorDTO resultado = completableFuture.get(5, TimeUnit.SECONDS);
        thenJugadorListo(resultado);
    }


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

    //ESTOS SON LOS MIOS PARA EL SPRINT 3

    @Test
    public void siAlguienSeUneALaSalaDeEsperaLosDemasJugadoresPuedenVerlo() throws Exception {
        String nombreUsuarioQueAcabaDeUnirseALaSala = "jose";
        CompletableFuture<MensajeRecibidoDTO> usuarioYaEnSalaDeEspera = givenUsuarioConectado("pepe","/topic" +
                "/cuandoUsuarioSeUneASalaDeEspera",false,null , MensajeRecibidoDTO.class);
        givenUsuarioConectado(nombreUsuarioQueAcabaDeUnirseALaSala,"/topic/cuandoUsuarioSeUneASalaDeEspera",
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
        thenIniciarLaPartida(mensaje);
    }


    @Test
    public void queSePuedaRedireccionarAUnUsuarioAUnaPartida(){

    }

    private void thenIniciarLaPartida(MensajeRecibidoDTO mensajeDelServidor) {
        assertEquals("http://localhost:8080/spring/juego", mensajeDelServidor.getMessage());
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
            session.send("/app/usuarioSeUneASalaDeEspera",new MensajeRecibidoDTO(nombreUsuario));
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



    private void dadoQueNoTengoJugadoresEnLaSalaDeEspera() {
    }
    public Jugador dadoQueTengoUnJugadorConNombre(String nombre) {

        return new Jugador(nombre);
    }

    public ModelAndView cuandoAgregoAUnJugadorALaSalaDeEspera(Jugador jugador) {
        SalaDeEsperaController controladorSalaDeEspera = new SalaDeEsperaController();
        ModelAndView mav = controladorSalaDeEspera.agregarJugadorALaSalaDeEspera(jugador);
        return mav;
    }

    public void entoncesRedirigoAlJugadorALaSalaDeEspera(ModelAndView mav) {
        assertThat(mav.getViewName(),equalToIgnoringCase("sala-de-espera"));
    }










}
