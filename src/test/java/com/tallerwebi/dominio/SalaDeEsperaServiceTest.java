package com.tallerwebi.dominio;

import com.tallerwebi.dominio.DTO.ListaUsuariosDTO;
import com.tallerwebi.dominio.DTO.MensajeDto;
import com.tallerwebi.dominio.DTO.MensajeRecibidoDTO;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.ServicioImplementacion.SalaDeEsperaServiceImpl;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.SalaDeEsperaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class SalaDeEsperaServiceTest {
    private SimpMessagingTemplate simpMessagingTemplate;

    //se prueba la implementacion del ServicioSalaDeEspera(interfaz)
    private SalaDeEsperaService servicioSalaDeEspera;
    private UsuarioPartidaRepository usuarioPartidaRepo;
    private PartidaRepository partidaRepo;
    private UsuarioRepository usuarioRepository;
    private PartidaService partidaService;
    private UsuarioPartidaService usuarioPartidaService;
    @BeforeEach
    public void setUp() {
        usuarioPartidaRepo = Mockito.mock(UsuarioPartidaRepository.class);
        simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        partidaRepo = mock(PartidaRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        this.servicioSalaDeEspera = new SalaDeEsperaServiceImpl(
                simpMessagingTemplate,
                usuarioPartidaRepo,
                partidaRepo,
                usuarioRepository,
                partidaService,
                usuarioPartidaService);
        ReflectionTestUtils.setField(servicioSalaDeEspera, "simpMessagingTemplate", simpMessagingTemplate);
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
        Partida partida = new Partida("partida","español",true,5,5,2,Estado.EN_ESPERA);

        doNothing().when(partidaRepo).actualizarEstado(idPartida, Estado.EN_CURSO);
        when(usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida)).thenReturn(usuarios);
        when(usuarioPartidaRepo.obtenerPartida(idPartida)).thenReturn(partida);

        whenSeRedireccionaALosUsuariosAUnaPartida(mensaje);

        thenRedireccionExitosa(usuarios);
    }

    @Test
    public void siSeRedireccionaALosUsuariosAUnaPartidaQueSeActualiceElEstadoDeLaPartidaAEnCurso() {
        Long idPartida = 1L;
        MensajeRecibidoDTO mensaje = new MensajeRecibidoDTO("iniciar partida",idPartida);
        Partida partida = new Partida("partida","español",true,5,5,2,Estado.EN_ESPERA);

        List<Usuario> usuarios = List.of(new Usuario("pepe"), new Usuario("Jose"));
        when(usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida)).thenReturn(usuarios);
        when(usuarioPartidaRepo.obtenerPartida(idPartida)).thenReturn(partida);

        whenSeRedireccionaALosUsuariosAUnaPartida(mensaje);
        verify(partidaRepo).actualizarEstado(any(Long.class), any(Estado.class));
    }
    //ESTOS SON LOS MIOS PARA EL SPRINT 4

    @Test
    public void SiNoSeCumpleConElMinimoDeUsuariosEstablecidoNoSePuedeRedireccionarAUnaPartida(){
        Long idPartida = 1L;
        Partida partida = new Partida("partida","español",true,5,5,3,Estado.EN_ESPERA);
        MensajeRecibidoDTO mensaje = new MensajeRecibidoDTO("iniciar partida",idPartida);
        List<Usuario> usuarios = List.of(new Usuario("pepe"), new Usuario("Jose"));

        when(usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida)).thenReturn(usuarios);
        when(usuarioPartidaRepo.obtenerPartida(idPartida)).thenReturn(partida);

        assertFalse(servicioSalaDeEspera.redireccionarUsuariosAPartida(mensaje));
    }
    @Test
    public void siAlguienAbandonaLaSalaDeEsperaQueSeLeCanceleLaPartidaAsociada(){
        Long idPartida = 1L;
        Long idUsuario = 1L;
        String nombreUsuario = "pepe";
        MensajeDto mensaje = new MensajeDto(idUsuario,idPartida,"abandona sala");

        whenSeAbandonaLaSalaDeEspera(mensaje,nombreUsuario);

        verify(usuarioPartidaRepo).borrarUsuarioPartidaAsociadaAlUsuario(any(Long.class),any(Long.class));
    }

    @Test
    public void siAlguienAbandonaLaSalaDeEsperaQueSeLeEnvieUnMensajeDeIrAlLobby(){
        Long idPartida = 1L;
        Long idUsuario = 1L;
        String nombreUsuario = "pepe";
        MensajeDto mensaje = new MensajeDto(idUsuario,idPartida,"abandona sala");
        doNothing().when(usuarioPartidaRepo).borrarUsuarioPartidaAsociadaAlUsuario(any(Long.class),any(Long.class));

        MensajeRecibidoDTO mensajeDelServidor = whenSeAbandonaLaSalaDeEspera(mensaje,nombreUsuario);

        thenAbandonoLaSala(mensajeDelServidor);
    }

    @Test
    public void siAlguienAbandonaLaSalaSeLeNotificaALosDemas(){
        Long idPartida = 1L;
        Long idUsuario = 1L;
        String nombreUsuario = "lucas";
        MensajeDto mensaje = new MensajeDto(idUsuario,idPartida,"abandona sala");
        List<Usuario> usuarios = List.of(new Usuario("pepe"), new Usuario("Jose"));
        when(usuarioPartidaRepo.obtenerUsuariosDeUnaPartida(idPartida)).thenReturn(usuarios);
        doNothing().when(usuarioPartidaRepo).borrarUsuarioPartidaAsociadaAlUsuario(idPartida,idUsuario);

        whenSeAbandonaLaSalaDeEspera(mensaje,nombreUsuario);

        thenNotificaALosOtrosUsuariosQueAbandonaLaSala(usuarios);
    }

    @Test
    public void siAlIntentarAbandonarLaSalaDeEsperaElIdDelUsuarioEsCeroQueSeObtengaElIdAlmacenadoEnRepositorio(){
        Long idPartida = 1L;
        Long idUsuario = 0L;
        String nombreUsuario = "lucas";
        Usuario usuario = new Usuario(nombreUsuario);
        MensajeDto mensajeDto = new MensajeDto(idUsuario,idPartida,"");

        when(this.usuarioRepository.obtenerUsuarioPorNombre(nombreUsuario)).thenReturn(usuario);
        doNothing().when(usuarioPartidaRepo).borrarUsuarioPartidaAsociadaAlUsuario(idPartida,idUsuario);

        servicioSalaDeEspera.abandonarSala(mensajeDto,nombreUsuario);

        verify(usuarioRepository).obtenerUsuarioPorNombre(nombreUsuario);
    }


    private void thenNotificaALosOtrosUsuariosQueAbandonaLaSala(List<Usuario> usuarios) {
        List<String> nombresEsperados = usuarios.stream()
                .map(Usuario::getNombreUsuario)
                .collect(Collectors.toList());
        for(Usuario usuario : usuarios) {
            verify(simpMessagingTemplate).convertAndSendToUser(
                    eq(usuario.getNombreUsuario()),
                    eq("/queue/jugadoresExistentes"),
                    argThat(dto -> dto instanceof ListaUsuariosDTO &&
                            new HashSet<>(((ListaUsuariosDTO) dto).getUsuarios()).equals(new HashSet<>(nombresEsperados))
                    )
            );
        }
    }

    private MensajeRecibidoDTO whenSeAbandonaLaSalaDeEspera(MensajeDto mensaje, String nombreUsuario) {
        return this.servicioSalaDeEspera.abandonarSala(mensaje,nombreUsuario);
    }


    private void thenAbandonoLaSala(MensajeRecibidoDTO mensaje) {
        assertEquals("http://localhost:8080/spring/lobby",mensaje.getMessage());
    }

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

    private void whenSeRedireccionaALosUsuariosAUnaPartida(MensajeRecibidoDTO mensaje) {
        servicioSalaDeEspera.redireccionarUsuariosAPartida(mensaje);
    }


}
