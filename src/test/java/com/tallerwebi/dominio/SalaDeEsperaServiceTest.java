package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.model.MensajeRecibidoDTO;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import com.tallerwebi.infraestructura.PartidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SalaDeEsperaServiceTest {
    private SimpMessagingTemplate simpMessagingTemplate;

    //se prueba la implementacion del ServicioSalaDeEspera(interfaz)
    private SalaDeEsperaService servicioSalaDeEspera;
    private UsuarioPartidaRepository usuarioPartidaRepo;
    private PartidaRepository partidaRepo;

    @BeforeEach
    public void setUp() {
        usuarioPartidaRepo = Mockito.mock(UsuarioPartidaRepository.class);
        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        partidaRepo = mock(PartidaRepository.class);
        this.servicioSalaDeEspera = new SalaDeEsperaServiceImpl(simpMessagingTemplate,usuarioPartidaRepo,partidaRepo);
    }

    @Test
    public void deberiaObtenerJugadoresDelFormularioCorrectamente() {
        // dado que tengo un mapa que viene del formulario de la sala de espera
        Map<String, String> parametros = Map.of(
                "jugador_1", "true",
                "jugador_2", "false",
                "otro_parametro", "irrelevante"
        );

        // cuando obtengo los jugadores del formulario y parseo la llave-valor
        Map<Long, Boolean> resultado = servicioSalaDeEspera.obtenerJugadoresDelFormulario(parametros);

        // entonces obtengo los jugadores con su llave-valor
        assertEquals(2, resultado.size());
        assertEquals(true, resultado.get(1L));
        assertEquals(false, resultado.get(2L));
    }

    @Test
    public void deberiaRetornarJugadoresNoListos() {
        // Datos de entrada simulados
        Map<Long, Boolean> jugadores = Map.of(
                1L, true,
                2L, false,
                3L, true,
                4L, false
        );

        // Llamada al método
        List<Long> resultado = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);

        // Verificaciones
        assertEquals(2, resultado.size());
        assertEquals(List.of(2L, 4L), resultado);
    }

    @Test
    public void deberiaRetornarListaVaciaSiTodosEstanListos() {
        // dado un mapa de jugadores con todos listos
        Map<Long, Boolean> jugadores = Map.of(
                1L, true,
                2L, true,
                3L, true
        );

        // cuando verifico si los jugadores que no estan listos
        List<Long> resultado = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);

        // entonces todos los jugadores estan listos
        assertEquals(0, resultado.size());
    }

    @Test
    public void deberiaRetornarListaVaciaSiNoHayJugadores() {
        // dado que no tengo jugadores
        Map<Long, Boolean> jugadores = Map.of();

        // cuando verifico los jugadores que no estan listos
        List<Long> resultado = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);

        // entonces no hay jugadores listos
        assertEquals(0, resultado.size());
    }


    //DE WEBSOCKETS

    @Test
    public void queSePuedanRedireccionarAUsuariosAUnaPartida() {
        Long idPartida = 1L;
        MensajeRecibidoDTO mensaje = new MensajeRecibidoDTO("iniciar partida",idPartida);

        List<Usuario> usuarios = List.of(new Usuario("pepe"), new Usuario("Jose"));
        when(usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida)).thenReturn(usuarios);

        thenSeRedireccionaALosUsuariosAUnaPartida(mensaje);

        thenRedireccionExitosa(usuarios);
    }


    /*@Test //ACA DEBERIA USAR EL HANDLER DE EXCEPTIONS CON @MessageExceptionHandler(UsuarioInvalidoException.class)
    @SendToUser("/queue/cantidadDeUsuariosInsuficientesParaIniciarPartida")
    public void SiNoSeCumpleConElMinimoDeUsuariosEstablecidoNoSePuedeRedireccionarAUnaPartida(){
        Long idPartida = 1L;
        MensajeRecibidoDTO mensaje = new MensajeRecibidoDTO("saraza",idPartida);
        List<String> usuarios = List.of("pepe", "jose");

        for (String usuario : usuarios) {
            verify(simpMessagingTemplate).convertAndSendToUser(
                    eq(usuario),
                    eq("/queue/irAPartida"),
                    argThat(dto -> dto instanceof MensajeRecibidoDTO &&
                            ((MensajeRecibidoDTO) dto).getMessage().equals("redirect::juego"))
            );
        }
    }*/











    private void thenRedireccionExitosa(List<Usuario> usuarios) {
        for (Usuario usuario : usuarios) {
            verify(simpMessagingTemplate).convertAndSendToUser(
                    eq(usuario.getNombreUsuario()),
                    eq("/queue/irAPartida"),
                    argThat(dto -> dto instanceof MensajeRecibidoDTO &&
                            ((MensajeRecibidoDTO) dto).getMessage().equals("http://localhost:8080/spring/juego"))
            );
        }
    }

    private void thenSeRedireccionaALosUsuariosAUnaPartida(MensajeRecibidoDTO mensaje) {
        servicioSalaDeEspera.redireccionarUsuariosAPartida(mensaje);
    }


}
