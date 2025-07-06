package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.DTO.EstadoJugadorDTO;
import com.tallerwebi.dominio.DTO.ListaUsuariosDTO;
import com.tallerwebi.dominio.DTO.MensajeDto;
import com.tallerwebi.dominio.DTO.MensajeRecibidoDTO;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.excepcion.CantidadDeUsuariosInsuficientesException;
import com.tallerwebi.dominio.excepcion.UsuarioInvalidoException;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SalaDeEsperaControllerTest {

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
    public void queUnJugadorPuedaEstarListo(){
        Long idPartida = 1L;
        Principal principal=  () -> "pepe";
        EstadoJugadorDTO estadoJugadorDTO = givenJugadorEnSala(idPartida,principal.getName(),true);
        //when(salaDeEsperaService.actualizarElEstadoDeUnUsuario(estadoJugadorDTO, principal.getName())).thenReturn(true);

        salaDeEsperaController.actualizarEstadoUsuario(estadoJugadorDTO,principal);

        verify(salaDeEsperaService).actualizarElEstadoDeUnUsuario(estadoJugadorDTO, principal.getName());
    }

    @Test
    public void siSeIntentoCambiarElEstadoDeOtroUsuarioQueDeError(){
        Long idPartida = 1L;
        Principal principal=  () -> "pepe";
        EstadoJugadorDTO estadoJugadorDTO = givenJugadorEnSala(idPartida,"jose",true);
        //when(salaDeEsperaService.actualizarElEstadoDeUnUsuario(estadoJugadorDTO, principal.getName())).thenReturn(false);

        assertThrows(UsuarioInvalidoException.class, ()-> salaDeEsperaController.actualizarEstadoUsuario(estadoJugadorDTO,principal));
        UsuarioInvalidoException ex = new UsuarioInvalidoException("Cantidad insuficiente de usuarios para iniciar partida");
        MensajeRecibidoDTO mensajeDeError = salaDeEsperaController.handleUsuarioInvalidoException(ex);
        assertEquals(mensajeDeError.getMessage(), ex.getMessage());
    }


    @Test
    public void queUnUsuarioSeUnaALaSalaDeEspera(){
        Long idPartida = 1L;
        Principal principal=  () -> "pepe";
        MensajeRecibidoDTO mensajeDeUnionASala = new MensajeRecibidoDTO("",idPartida);

        salaDeEsperaController.usuarioSeUneASala(mensajeDeUnionASala,principal);

        verify(salaDeEsperaService).mostrarAUnUsuarioLosUsuariosExistentesEnSala(principal.getName(),idPartida);
    }


    @Test
    public void queSePuedaIniciarLaPartida(){
        Long idPartida = 1L;
        MensajeRecibidoDTO mensajeParaIniciarPartida = new MensajeRecibidoDTO("mensaje de inicio de partida",idPartida);
        //when(salaDeEsperaService.redireccionarUsuariosAPartida(mensajeParaIniciarPartida));

        salaDeEsperaController.enviarUsuariosALaPartida(mensajeParaIniciarPartida);
        verify(salaDeEsperaService).redireccionarUsuariosAPartida(mensajeParaIniciarPartida);
    }

    //ESTOS SON LOS MIOS PARA EL SPRINT 4


    @Test
    public void siNoSePudoIniciarLaPartidaQueDeError() {
        MensajeRecibidoDTO dto = new MensajeRecibidoDTO("mensaje", 1L);

        //when(salaDeEsperaService.redireccionarUsuariosAPartida(any())).thenReturn(false);

        assertThrows(CantidadDeUsuariosInsuficientesException.class, () -> {
            salaDeEsperaController.enviarUsuariosALaPartida(dto);
        });

        CantidadDeUsuariosInsuficientesException ex = new CantidadDeUsuariosInsuficientesException("error");
        MensajeRecibidoDTO respuesta = salaDeEsperaController.enviarMensajeDeDenegacionDeAvanceAPartida(ex);
        assertEquals("error", respuesta.getMessage());
    }

    @Test
    public void queUnJugadorPuedaAbandonarLaSalaDeEspera(){
        MensajeDto mensajeDto = new MensajeDto(1L,1L,"me voy");
        MensajeRecibidoDTO msgEsperado = new MensajeRecibidoDTO("http://localhost:8080/spring/lobby");
        when(salaDeEsperaService.abandonarSala(mensajeDto,"pepe")).thenReturn(msgEsperado);


        Principal principal = () -> "pepe";
        MensajeRecibidoDTO resultado = salaDeEsperaController.abandonarSala(mensajeDto, principal);

        assertEquals(msgEsperado, resultado);
        verify(salaDeEsperaService).abandonarSala(mensajeDto, "pepe");
    }


    @Test
    public void siAlUnirseAUnaSalaYaExisteCombinacionUsuarioYPartidaQueNoSeRegistreNuevamente(){
        Long idPartida = 1L;
        Long idUsuario = 1L;
        String nombreUsuario = "lucas";
        Usuario usuario = new Usuario(nombreUsuario);
        Partida partida = new Partida();
        UsuarioPartida usuarioPartida = new UsuarioPartida(usuario,partida,0, false, Estado.EN_ESPERA);

        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);

        when(session.getAttribute("idUsuario")).thenReturn(idUsuario);
        when(session.getAttribute("idPartida")).thenReturn(idPartida);
        when(usuarioPartidaService.buscarUsuarioPartida(idPartida, idUsuario)).thenReturn(usuarioPartida);
        when(usuarioService.obtenerNombrePorId(idUsuario)).thenReturn(nombreUsuario);
        when(partidaService.verificarEstadoDeLaPartida(idPartida)).thenReturn(Estado.EN_ESPERA);

        salaDeEsperaController.manejarSalaDeEspera(null, model, session);

        verify(usuarioPartidaService, never()).agregarUsuarioAPartida(any(), any(), anyInt(), anyBoolean(), any());
    }















    private EstadoJugadorDTO givenJugadorEnSala(Long idPartida, String nombre, boolean estaListo) {
        EstadoJugadorDTO estadoJugadorDTO = new EstadoJugadorDTO();
        estadoJugadorDTO.setUsername(nombre);
        estadoJugadorDTO.setEstaListo(estaListo);
        estadoJugadorDTO.setIdPartida(idPartida);
        return estadoJugadorDTO;
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
