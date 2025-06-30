package com.tallerwebi.dominio;

import com.tallerwebi.dominio.PartidaServiceImpl;
import com.tallerwebi.dominio.RondaDto;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.AciertoService;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.RondaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PartidaServiceTest {

    private PartidaServiceImpl partidaService;
    private SimpMessagingTemplate simpMessagingTemplate;
    private PartidaRepository partidaRepository;
    private RondaService rondaService;
    private RondaRepository rondaRepositorio;
    private UsuarioPartidaRepository usuarioPartidaRepository;
    private AciertoService aciertoService;
    private UsuarioPartidaService usuarioPartidaService;

    @BeforeEach
    public void setUp() throws IllegalAccessException, NoSuchFieldException {
        simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        partidaRepository = mock(PartidaRepository.class);
        rondaService = mock(RondaService.class);
        rondaRepositorio = mock(RondaRepository.class);
        usuarioPartidaRepository = mock(UsuarioPartidaRepository.class);
        aciertoService = mock(AciertoService.class);
        usuarioPartidaService= mock(UsuarioPartidaService.class);

        partidaService = new PartidaServiceImpl(
                simpMessagingTemplate,
                partidaRepository,
                rondaService,
                aciertoService,
                usuarioPartidaService,
                usuarioPartidaRepository,
                rondaRepositorio
        );

        Field usuarioPartidaRepoField = PartidaServiceImpl.class.getDeclaredField("usuarioPartidaRepository");
        usuarioPartidaRepoField.setAccessible(true);
        usuarioPartidaRepoField.set(partidaService, usuarioPartidaRepository);
    }

    @Test
    public void dadoPartidaValidaCuandoSeIniciaNuevaRondaDevuelveRondaDtoConDatosCorrectos() throws Exception {
        Long partidaId = 1L;

        Partida2 partidaMock = crearPartidaMock("Castellano");
        Palabra palabra = crearPalabraConDefinicion("Casa", "Lugar donde vive la gente");
        Ronda ronda = crearRonda(palabra, 1);
        UsuarioPartida usuarioPartida = crearUsuarioPartida("july3p", 20);

        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partidaMock);
        when(rondaService.crearRonda(partidaId, "Castellano")).thenReturn(ronda);
        when(usuarioPartidaRepository.obtenerUsuarioPartidaPorPartida(partidaId)).thenReturn(List.of(usuarioPartida));

        // Inyección por reflexión
        Field usuarioPartidaRepoField = PartidaServiceImpl.class.getDeclaredField("usuarioPartidaRepository");
        usuarioPartidaRepoField.setAccessible(true);
        usuarioPartidaRepoField.set(partidaService, usuarioPartidaRepository);

        RondaDto resultado = partidaService.iniciarNuevaRonda(partidaId);

        assertNotNull(resultado);
        assertEquals("Casa", resultado.getPalabra());
        assertEquals("Lugar donde vive la gente", resultado.getDefinicionTexto());
        assertEquals(1, resultado.getNumeroDeRonda());

        List<JugadorPuntajeDto> jugadores = resultado.getJugadores();
        assertEquals(1, jugadores.size());

        JugadorPuntajeDto jugador = jugadores.get(0);
        assertEquals("july3p", jugador.getNombre());
        assertEquals(20, jugador.getPuntaje());

        // Capturamos las llamadas a convertAndSend
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(simpMessagingTemplate, times(2)).convertAndSend(eq("/topic/juego/" + partidaId), captor.capture());

        List<Object> enviados = captor.getAllValues();

        // Verificamos que uno de los mensajes es el RondaDto esperado
        boolean encontroRondaDto = enviados.stream().anyMatch(o -> o instanceof RondaDto);

        assertTrue(encontroRondaDto, "No se envió RondaDto por simpMessagingTemplate");

        // Opcional: Podés verificar también el otro tipo de mensaje si querés
    }


    @Test
    public void dadoPartidaInexistenteCuandoSeIniciaNuevaRondaLanzaExcepcion() {
        Long partidaId = 999L;
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> partidaService.iniciarNuevaRonda(partidaId));

        assertEquals("No existe la partida con ID: " + partidaId, ex.getMessage());
    }

    @Test
    public void dadoPartidaIdCuandoSeObtieneRondaActualDevuelveLaDefinicionCorrecta() {
        Long partidaId = 77L;

        RondaDto dto = new RondaDto();
        dto.setPalabra("Sol");
        dto.setDefinicionTexto("Estrella del sistema solar");
        dto.setNumeroDeRonda(2);

        // Inyectamos el DTO en el mapa privado vía método de test
        partidaService.obtenerMapaDefinicionesParaTest().put(partidaId, dto);

        RondaDto resultado = partidaService.obtenerPalabraYDefinicionDeRondaActual(partidaId);

        assertNotNull(resultado);
        assertEquals("Sol", resultado.getPalabra());
        assertEquals("Estrella del sistema solar", resultado.getDefinicionTexto());
        assertEquals(2, resultado.getNumeroDeRonda());
    }


    // ──────────────────────
    // MÉTODOS PRIVADOS AUXILIARES
    // ──────────────────────

    private Partida2 crearPartidaMock(String idioma) {
        Partida2 partida = new Partida2();
        partida.setIdioma(idioma);
        return partida;
    }

    private Palabra crearPalabraConDefinicion(String descripcion, String definicionTexto) {
        Palabra palabra = new Palabra();
        palabra.setDescripcion(descripcion);

        Definicion definicion = new Definicion();
        definicion.setDefinicion(definicionTexto);

        palabra.setDefiniciones(List.of(definicion));
        return palabra;
    }

    private Ronda crearRonda(Palabra palabra, int numero) {
        Ronda ronda = new Ronda();
        ronda.setPalabra(palabra);
        ronda.setNumeroDeRonda(numero);
        return ronda;
    }

    private UsuarioPartida crearUsuarioPartida(String nombreUsuario, int puntaje) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);

        UsuarioPartida up = new UsuarioPartida();
        up.setUsuario(usuario);
        up.setPuntaje(puntaje);
        return up;
    }
}

