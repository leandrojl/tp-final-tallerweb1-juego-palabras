package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.excepcion.PartidaInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistente;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.AciertoRepository;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.UsuarioPartidaRepository;
import com.tallerwebi.infraestructura.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JuegoControllerTest {

    @InjectMocks
    private JuegoController controladorJuego;

    @Mock
    private RondaService rondaServicio;
    @Mock
    private PuntajeService puntajeServicio;
    @Mock
    private PartidaService partidaServicio;
    @Mock
    private UsuarioService usuarioServicio;
    @Mock
    private PalabraService palabraServicio;
    @Mock
    private AciertoService aciertoServicio;

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PartidaRepository partidaRepository;
    @Mock
    private RondaRepository rondaRepository;
    @Mock
    private PalabraRepository palabraRepository;
    @Mock
    private UsuarioPartidaRepository usuarioPartidaRepository;
    @Mock
    private AciertoRepository aciertoRepository;
    private Usuario usuario;
    private Partida2 partida;
    private UsuarioPartida usuarioPartida;
    private Ronda ronda;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // ← Acá se inicializan los mocks

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombreUsuario("UsuarioTest");

        partida = new Partida2();
        partida.setId(1L);
        partida.setNombre("PartidaTest");
        partida.setEstado(Estado.EN_CURSO);
        partida.setRondasTotales(3);
        partida.setIdioma("Español");

        usuarioPartida = new UsuarioPartida();
        usuarioPartida.setUsuario(usuario);
        usuarioPartida.setPartida(partida);
        usuarioPartida.setPuntaje(100);

        ronda = new Ronda();
        ronda.setId(10L);
        ronda.setNumeroDeRonda(1);
        ronda.setEstado(Estado.EN_CURSO);
        ronda.setPartida(partida);
        ronda.setDefinicion("Definición de prueba");
        ronda.setPalabra(new Palabra());
    }


    @Test
    public void mostrarVistaJuego_lanzaException_siUsuarioNoExiste() {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(null);

        Exception exception = assertThrows(UsuarioInexistente.class, () -> {
            controladorJuego.mostrarVistaJuego(usuarioId, partidaId);
        });

    }

    @Test
    public void mostrarVistaJuego_lanzaException_siPartidaNoExiste() {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(null);

        Exception exception = assertThrows(PartidaInexistente.class, () -> {
            controladorJuego.mostrarVistaJuego(usuarioId, partidaId);
        });
    }

    @Test
    public void queSeMuestreLaVistaJuego() throws Exception {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(usuarioPartidaRepository.buscarPorPartidaId(partidaId)).thenReturn(List.of(usuarioPartida));

        ModelAndView mov = controladorJuego.mostrarVistaJuego(usuarioId, partidaId);

        assertEquals("juego", mov.getViewName());
    }


    @Test
    public void queSeReciba_IdJugador_y_IdPartida_AlCargarElJuego() throws Exception {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(usuarioPartidaRepository.buscarPorPartidaId(partidaId)).thenReturn(List.of(usuarioPartida));

        ModelAndView mov = controladorJuego.mostrarVistaJuego(usuarioId, partidaId);


        assertEquals(usuarioId, mov.getModel().get("usuarioId"));
        assertEquals(partidaId, mov.getModel().get("partidaId"));
        assertEquals("Definición de prueba", mov.getModel().get("definicion"));
    }

    @Test
    public void queSeAgreguenLosJugadoresAlModelo() throws Exception {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);

        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        otroUsuario.setNombreUsuario("SegundoJugador");

        UsuarioPartida usuarioPartida2 = new UsuarioPartida();
        usuarioPartida2.setUsuario(otroUsuario);
        usuarioPartida2.setPartida(partida);
        usuarioPartida2.setPuntaje(80);

        when(usuarioPartidaRepository.buscarPorPartidaId(partidaId)).thenReturn(List.of(usuarioPartida, usuarioPartida2));

        ModelAndView mov = controladorJuego.mostrarVistaJuego(usuarioId, partidaId);

        List<Map<String, Object>> jugadores = (List<Map<String, Object>>) mov.getModel().get("jugadores");

        assertNotNull(jugadores);
        assertEquals(2, jugadores.size());
        //jugadores.stream() Convierte la lista jugadores en un Stream, que permite recorrerla y aplicar filtros o búsquedas..
        // anyMatch(...) Busca si algún elemento del stream cumple la condición que le paso.
        assertTrue(jugadores.stream().anyMatch(j -> j.get("nombre").equals("UsuarioTest")));
        assertTrue(jugadores.stream().anyMatch(j -> j.get("nombre").equals("SegundoJugador")));
    }

    @Test
    public void queSeAgregueLaPalabraAlModelo() throws Exception {

        Palabra palabrax = new Palabra();
        palabrax.setDescripcion("palabraEjemplo");

        ronda.setPalabra(palabrax);


        when(usuarioRepository.buscarPorId(1L)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(1L)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(1L, 1L)).thenReturn(usuarioPartida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(1L)).thenReturn(ronda);
        when(usuarioPartidaRepository.buscarPorPartidaId(1L)).thenReturn(List.of(usuarioPartida));

        ModelAndView mov = controladorJuego.mostrarVistaJuego(1L, 1L);

        assertEquals("palabraEjemplo", ((Palabra) ronda.getPalabra()).getDescripcion());
    }

    @Test
    void queSeMuestreLaVistaJuegoConDatosValidos() throws PartidaInexistente, UsuarioInexistente {
        Long usuarioId = 1L;
        Long partidaId = 10L;

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNombreUsuario("july");

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setEstado(Estado.EN_ESPERA);
        partida.setRondasTotales(3);
        partida.setIdioma("ES");

        Palabra palabra = new Palabra();
        palabra.setDescripcion("perro");

        Definicion def = new Definicion();
        def.setDefinicion("Animal doméstico.");

        Ronda ronda = new Ronda();
        ronda.setNumeroDeRonda(1);
        ronda.setPalabra(palabra);
        ronda.setDefinicion("Animal doméstico.");
        ronda.setEstado(Estado.EN_CURSO);

        UsuarioPartida usuarioPartida = new UsuarioPartida();
        usuarioPartida.setUsuario(usuario);
        usuarioPartida.setPartida(partida);
        usuarioPartida.setPuntaje(0);

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(null);

        Map<Palabra, List<Definicion>> palabraMap = new HashMap<>();
        palabraMap.put(palabra, List.of(def));

        when(palabraServicio.traerPalabraYDefinicion("ES")).thenReturn((HashMap<Palabra, List<Definicion>>) palabraMap);
        when(rondaRepository.guardar(any())).thenReturn(ronda);
        when(usuarioPartidaRepository.buscarPorPartidaId(partidaId)).thenReturn(List.of(usuarioPartida));

        ModelAndView mav = controladorJuego.mostrarVistaJuego(usuarioId, partidaId);

        assertEquals("juego", mav.getViewName());
        assertEquals("Animal doméstico.", mav.getModel().get("definicion"));
        assertEquals(1, mav.getModel().get("rondaActual"));
        assertEquals("perro", ((Palabra) mav.getModel().get("palabra")).getDescripcion());
    }

    @Test
    void queElAciertoDebeSumePuntosYAvanceDeRonda() {
        Long usuarioId = 1L;
        Long partidaId = 10L;
        int tiempoRestante = 50;
        String intento = "perro";

        // Mocks necesarios
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNombreUsuario("july");

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setRondasTotales(3);
        partida.setEstado(Estado.EN_CURSO);
        partida.setIdioma("ES");

        Palabra palabra = new Palabra();
        palabra.setDescripcion("perro");

        Ronda rondaActual = new Ronda();
        rondaActual.setId(100L);
        rondaActual.setPartida(partida);
        rondaActual.setPalabra(palabra);
        rondaActual.setDefinicion("Animal doméstico.");
        rondaActual.setNumeroDeRonda(1);
        rondaActual.setEstado(Estado.EN_CURSO);

        UsuarioPartida usuarioPartida = new UsuarioPartida();
        usuarioPartida.setUsuario(usuario);
        usuarioPartida.setPartida(partida);
        usuarioPartida.setPuntaje(0);

        // Nueva palabra/definición para siguiente ronda
        Palabra nuevaPalabra = new Palabra();
        nuevaPalabra.setDescripcion("gato");

        Definicion def = new Definicion();
        def.setDefinicion("Animal felino.");

        Ronda nuevaRonda = new Ronda();
        nuevaRonda.setId(200L);
        nuevaRonda.setPartida(partida);
        nuevaRonda.setPalabra(nuevaPalabra);
        nuevaRonda.setDefinicion("Animal felino.");
        nuevaRonda.setNumeroDeRonda(2);
        nuevaRonda.setEstado(Estado.EN_CURSO);

        HashMap<Palabra, List<Definicion>> palabraMap = new HashMap<>();
        palabraMap.put(nuevaPalabra, List.of(def));

        // Mockeo de dependencias
        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(rondaActual);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(palabraServicio.traerPalabraYDefinicion("ES")).thenReturn(palabraMap);
        when(rondaRepository.guardar(any())).thenReturn(nuevaRonda);

        // Ejecutar
        Map<String, Object> response = controladorJuego.procesarIntentoAjax(intento, usuarioId, partidaId, tiempoRestante);

        // Verificaciones
        assertEquals(true, response.get("correcto"));
        assertEquals(2, response.get("rondaActual"));
        assertEquals("gato", response.get("nuevaPalabra"));
        assertEquals("Animal felino.", response.get("nuevaDefinicion"));
        assertEquals(false, response.get("partidaTerminada"));
        assertEquals(100, response.get("puntaje")); // por tiempoRestante > 45
    }

    @Test
    void queSiSeAgotaElTiempoDebeDarCeroPuntos() {
        Long usuarioId = 1L;
        Long partidaId = 10L;
        int tiempoRestante = 0;
        String intento = "perro"; // palabra correcta

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setRondasTotales(3);
        partida.setEstado(Estado.EN_CURSO);
        partida.setIdioma("ES");

        Palabra palabra = new Palabra();
        palabra.setDescripcion("perro");

        Ronda ronda = new Ronda();
        ronda.setId(1L);
        ronda.setPartida(partida);
        ronda.setPalabra(palabra);
        ronda.setNumeroDeRonda(1);
        ronda.setEstado(Estado.EN_CURSO);

        UsuarioPartida usuarioPartida = new UsuarioPartida();
        usuarioPartida.setUsuario(usuario);
        usuarioPartida.setPartida(partida);
        usuarioPartida.setPuntaje(0);

        Palabra nuevaPalabra = new Palabra();
        nuevaPalabra.setDescripcion("gato");

        Definicion def = new Definicion();
        def.setDefinicion("Animal felino.");

        HashMap<Palabra, List<Definicion>> palabraMap = new HashMap<>();
        palabraMap.put(nuevaPalabra, List.of(def));

        Ronda nuevaRonda = new Ronda();
        nuevaRonda.setId(2L);
        nuevaRonda.setNumeroDeRonda(2);
        nuevaRonda.setPartida(partida);
        nuevaRonda.setPalabra(nuevaPalabra);
        nuevaRonda.setDefinicion("Animal felino.");
        nuevaRonda.setEstado(Estado.EN_CURSO);

        // Mocks
        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(palabraServicio.traerPalabraYDefinicion("ES")).thenReturn(palabraMap);
        when(rondaRepository.guardar(any())).thenReturn(nuevaRonda);

        // Ejecutar
        Map<String, Object> response = controladorJuego.procesarIntentoAjax(intento, usuarioId, partidaId, tiempoRestante);

        // Verificaciones
        assertEquals(true, response.get("correcto")); // acertó la palabra
        assertEquals(0, response.get("puntaje"));     // pero no suma puntos
        assertEquals(2, response.get("rondaActual")); // se avanza igual
    }

    @Test
    void queAlHacerElIntentoYElJugadorNoAcierteNoSumePuntosNiAvanceRonda() {
        Long usuarioId = 1L;
        Long partidaId = 10L;
        int tiempoRestante = 30;
        String intento = "gato"; // palabra incorrecta

        // Usuario y partida simulados
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setEstado(Estado.EN_CURSO);
        partida.setRondasTotales(3);
        partida.setIdioma("ES");

        Palabra palabra = new Palabra();
        palabra.setDescripcion("perro"); // palabra correcta

        Ronda ronda = new Ronda();
        ronda.setId(100L);
        ronda.setPartida(partida);
        ronda.setPalabra(palabra);
        ronda.setNumeroDeRonda(1);
        ronda.setEstado(Estado.EN_CURSO);

        UsuarioPartida usuarioPartida = new UsuarioPartida();
        usuarioPartida.setUsuario(usuario);
        usuarioPartida.setPartida(partida);
        usuarioPartida.setPuntaje(0);

        // Mocks
        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);

        // Ejecutar
        Map<String, Object> response = controladorJuego.procesarIntentoAjax(intento, usuarioId, partidaId, tiempoRestante);

        // Verificaciones
        assertEquals(false, response.get("correcto"));
        assertEquals(false, response.get("partidaTerminada")); // No terminó aún
        assertFalse(response.containsKey("nuevaPalabra"));     // No se generó nueva ronda
        assertEquals(0, usuarioPartida.getPuntaje());          // Puntaje no cambia
    }

    @Test
    void queSiElJugadorAciertaLaUltimaRondaTermineLaPartida() {
        Long usuarioId = 1L;
        Long partidaId = 10L;
        int tiempoRestante = 20;
        String intento = "perro";

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setRondasTotales(5);
        partida.setEstado(Estado.EN_CURSO);
        partida.setIdioma("ES");

        Palabra palabra = new Palabra();
        palabra.setDescripcion("perro");

        Ronda ronda = new Ronda();
        ronda.setId(200L);
        ronda.setPartida(partida);
        ronda.setPalabra(palabra);
        ronda.setNumeroDeRonda(5);
        ronda.setEstado(Estado.EN_CURSO);

        UsuarioPartida usuarioPartida = new UsuarioPartida();
        usuarioPartida.setUsuario(usuario);
        usuarioPartida.setPartida(partida);
        usuarioPartida.setPuntaje(50);

        // Mocks
        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);

        // Ejecutar
        Map<String, Object> response = controladorJuego.procesarIntentoAjax(intento, usuarioId, partidaId, tiempoRestante);

        // Verificaciones
        assertEquals(true, response.get("correcto"));
        assertEquals(true, response.get("partidaTerminada"));
        assertEquals(100, response.get("puntaje"));
        verify(partidaRepository).actualizarEstado(partidaId, Estado.FINALIZADA);
    }
    @Test
    void queSiSeTerminaElTiempoSinAciertoDebeFinalizarRondaActualYCrearNueva() {
        Long usuarioId = 1L;
        Long partidaId = 10L;

        // Mock de usuario y partida
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setIdioma("ES");
        partida.setRondasTotales(3);

        // Ronda actual
        Ronda rondaActual = new Ronda();
        rondaActual.setId(1L);
        rondaActual.setNumeroDeRonda(1);
        rondaActual.setPartida(partida);
        rondaActual.setEstado(Estado.EN_CURSO);

        // Nueva palabra/definición
        Palabra nuevaPalabra = new Palabra();
        nuevaPalabra.setDescripcion("gato");

        Definicion def = new Definicion();
        def.setDefinicion("Animal felino.");

        HashMap<Palabra, List<Definicion>> palabraMap = new HashMap<>();
        palabraMap.put(nuevaPalabra, List.of(def));

        Ronda nuevaRonda = new Ronda();
        nuevaRonda.setId(2L);
        nuevaRonda.setNumeroDeRonda(2);
        nuevaRonda.setPartida(partida);
        nuevaRonda.setEstado(Estado.EN_CURSO);
        nuevaRonda.setPalabra(nuevaPalabra);
        nuevaRonda.setDefinicion("Animal felino.");

        // Mocks
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(rondaActual);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(palabraServicio.traerPalabraYDefinicion("ES")).thenReturn(palabraMap);
        when(rondaRepository.guardar(any())).thenReturn(nuevaRonda);

        // Ejecutar
        Map<String, Object> response = controladorJuego.finRonda(usuarioId, partidaId);

        // Verificaciones
        assertEquals(false, response.get("partidaTerminada"));
        assertEquals(2, response.get("rondaActual"));
        assertEquals("gato", response.get("nuevaPalabra"));
        assertEquals("Animal felino.", response.get("nuevaDefinicion"));

        verify(rondaRepository).actualizar(rondaActual);
    }

    @Test
    void queSiNoHuboAciertoEnUltimaRondaDebeTerminarPartida() {
        Long usuarioId = 1L;
        Long partidaId = 10L;

        // Partida con 2 rondas totales
        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setRondasTotales(2);
        partida.setIdioma("ES");

        // Ronda actual es la 2da (última)
        Ronda ronda = new Ronda();
        ronda.setId(2L);
        ronda.setNumeroDeRonda(2);
        ronda.setPartida(partida);
        ronda.setEstado(Estado.EN_CURSO);

        // Mocks
        when(rondaRepository.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);

        // Ejecutar
        Map<String, Object> response = controladorJuego.finRonda(usuarioId, partidaId);

        // Verificar que se terminó la partida
        assertEquals(true, response.get("partidaTerminada"));
        verify(rondaRepository).actualizar(ronda); // ronda se cerró
        verify(partidaRepository).actualizarEstado(partidaId, Estado.FINALIZADA);

        // No se debe crear una nueva ronda → no contiene nuevaPalabra
        assertFalse(response.containsKey("nuevaPalabra"));
        assertFalse(response.containsKey("nuevaDefinicion"));
    }

        @Test
    void queLaVistaDeMostrarResultadosMuestreRankingOrdenadoYGanador() {
        Long usuarioId = 1L;
        Long partidaId = 10L;

        Usuario jugador1 = new Usuario();
        jugador1.setId(1L);
        jugador1.setNombreUsuario("july");

        Usuario jugador2 = new Usuario();
        jugador2.setId(2L);
        jugador2.setNombreUsuario("leonel");

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setNombre("Partida Test");

        UsuarioPartida up1 = new UsuarioPartida();
        up1.setUsuario(jugador1);
        up1.setPartida(partida);
        up1.setPuntaje(80);

        UsuarioPartida up2 = new UsuarioPartida();
        up2.setUsuario(jugador2);
        up2.setPartida(partida);
        up2.setPuntaje(100);

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(jugador1);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorPartidaId(partidaId)).thenReturn(List.of(up1, up2));

        ModelAndView mav = controladorJuego.mostrarResultados(usuarioId, partidaId);

        assertEquals("resultados", mav.getViewName());

        List<Map<String, Object>> ranking = (List<Map<String, Object>>) mav.getModel().get("ranking");

        // Ganador debería ser leonel
        assertEquals("leonel", mav.getModel().get("ganador"));
        assertEquals("july", mav.getModel().get("jugadorActual"));
        assertEquals("Partida Test", mav.getModel().get("nombrePartida"));

        // Verificamos que el primero del ranking sea el que tiene 100 puntos
        assertEquals("leonel", ranking.get(0).get("nombre"));
        assertEquals(100, ranking.get(0).get("puntaje"));
        assertEquals(true, ranking.get(1).get("esJugadorActual")); // "july"
    }
    @Test
    void queSiLaVistaResultadosNoTieneJugadoresMuestreMensajeError() {
        Long usuarioId = 1L;
        Long partidaId = 10L;

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNombreUsuario("july");

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setNombre("Partida sin jugadores");

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorPartidaId(partidaId)).thenReturn(List.of());

        ModelAndView mav = controladorJuego.mostrarResultados(usuarioId, partidaId);

        assertEquals("resultados", mav.getViewName());
        assertEquals("No hay jugadores en esta partida.", mav.getModel().get("mensajeError"));

    }
    @Test
    void queSiHayEmpateDebeArmarRankingCorrecto() {
        Long usuarioId = 1L;
        Long partidaId = 20L;

        Usuario jugador1 = new Usuario();
        jugador1.setId(1L);
        jugador1.setNombreUsuario("july");

        Usuario jugador2 = new Usuario();
        jugador2.setId(2L);
        jugador2.setNombreUsuario("leonel");

        Partida2 partida = new Partida2();
        partida.setId(partidaId);
        partida.setNombre("Empate Test");

        UsuarioPartida up1 = new UsuarioPartida();
        up1.setUsuario(jugador1);
        up1.setPartida(partida);
        up1.setPuntaje(100); // empate

        UsuarioPartida up2 = new UsuarioPartida();
        up2.setUsuario(jugador2);
        up2.setPartida(partida);
        up2.setPuntaje(100); // empate

        when(usuarioRepository.buscarPorId(usuarioId)).thenReturn(jugador1);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaRepository.buscarPorPartidaId(partidaId)).thenReturn(List.of(up1, up2));

        ModelAndView mav = controladorJuego.mostrarResultados(usuarioId, partidaId);

        assertEquals("resultados", mav.getViewName());

        List<Map<String, Object>> ranking = (List<Map<String, Object>>) mav.getModel().get("ranking");

        assertEquals(2, ranking.size());
        assertEquals(100, ranking.get(0).get("puntaje"));
        assertEquals(100, ranking.get(1).get("puntaje"));

        // El ganador puede ser cualquiera de los dos, dependiendo del orden
        String ganador = (String) mav.getModel().get("ganador");
        assertTrue(ganador.equals("july") || ganador.equals("leonel"));

        // El jugador actual está marcado
        Map<String, Object> actual = ranking.stream()
                .filter(j -> j.get("nombre").equals("july"))
                .findFirst().orElseThrow();

        assertTrue((Boolean) actual.get("esJugadorActual"));
    }

/*


    @Test
    public void queSeRecibaElIntentoAlIntentarAcertar() {
        String intento = "example";
        String idJugador = "1";
        int tiempoRestante = 50;

        partidaServicio.iniciarNuevaPartida(idJugador, "Gian");
        partidaServicio.obtenerPartida(idJugador).avanzarRonda("example", "Definicion ejemplo");

        Map<String, Object> resultado = controladorJuego.procesarIntentoAjax(intento, idJugador, tiempoRestante);

        assertEquals(true, resultado.get("correcto"));
        assertEquals(100, resultado.get("puntaje"));
    }

    @Test
    public void queFinaliceLaPartidaAlLlegarALaUltimaRonda() {
        String jugadorId = "1";
        partidaServicio.iniciarNuevaPartida(jugadorId, "july3p");

        for (int i = 0; i < 5; i++) {
            partidaServicio.obtenerPartida(jugadorId).avanzarRonda("palabra" + i, "definición" + i);
        }

        assertTrue(partidaServicio.obtenerPartida(jugadorId).isPartidaTerminada());
    }

    @Test
    public void queSeMuestreVistaFinalConRankingYGanador() {
        String jugadorId = "1";
        String nombre = "July3p";

        Jugador jugador = new Jugador(jugadorId, nombre, "july3p@hotmail.com", "pass");
        partidaServicio.iniciarNuevaPartida(jugadorId, nombre);
        puntajeServicio.registrarJugador(jugadorId, jugador);
        puntajeServicio.registrarPuntos(jugadorId, 500);

        Model model = new ExtendedModelMap();
        String viewName = controladorJuego.mostrarVistaFinal(jugadorId, model);

        assertEquals("vistaFinalJuego", viewName);
        assertNotNull(model.getAttribute("ranking"));
        assertEquals(nombre, model.getAttribute("ganador"));
        assertEquals(nombre, model.getAttribute("jugadorActual"));
    }*/
}
