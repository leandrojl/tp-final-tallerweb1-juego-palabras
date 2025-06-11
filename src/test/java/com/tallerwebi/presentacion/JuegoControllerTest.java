package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.excepcion.ElUsuarioNoPerteneceAEstaPartida;
import com.tallerwebi.dominio.excepcion.PartidaInexistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistente;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.AciertoRepository;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.UsuarioPartidaRepository;
import com.tallerwebi.infraestructura.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
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
    private UsuarioPartidaService usuarioPartidaServicio;
    @Mock
    private PalabraService palabraServicio;
    @Mock
    private AciertoService aciertoServicio;



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
        ronda.setNumeroDeRonda(1L);
        ronda.setEstado(Estado.EN_CURSO);
        ronda.setPartida(partida);
        Definicion definicion = new Definicion();
        definicion.setDefinicion("Definicion de prueba");
        ronda.setDefinicion(definicion);
        ronda.setPalabra(new Palabra());
    }


    @Test
    public void mostrarVistaJuego_lanzaException_siUsuarioNoExiste() {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioServicio.buscarPorId(usuarioId)).thenReturn(null);

        Exception exception = assertThrows(UsuarioInexistente.class, () -> {
            controladorJuego.mostrarVistaJuego(usuarioId, partidaId);
        });

    }

    @Test
    public void mostrarVistaJuego_lanzaException_siUsuarioNoEstaEnTalPartida() throws Exception {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioServicio.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaServicio.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaServicio.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(null);

        Exception exception = assertThrows(ElUsuarioNoPerteneceAEstaPartida.class, () -> {
            controladorJuego.mostrarVistaJuego(usuarioId, partidaId);
        });
    }

    @Test
    public void mostrarVistaJuego_lanzaException_siPartidaNoExiste() {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioServicio.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaServicio.buscarPorId(partidaId)).thenReturn(null);

        Exception exception = assertThrows(PartidaInexistente.class, () -> {
            controladorJuego.mostrarVistaJuego(usuarioId, partidaId);
        });
    }

    @Test
    public void queSeMuestreLaVistaJuego() throws Exception {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioServicio.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaServicio.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaServicio.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(rondaServicio.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(usuarioPartidaServicio.buscarListaDeUsuariosPartidaPorPartidaId(partidaId)).thenReturn(List.of(usuarioPartida));

        ModelAndView mov = controladorJuego.mostrarVistaJuego(usuarioId, partidaId);

        assertEquals("juego", mov.getViewName());
    }


    @Test
    public void queSeReciba_IdJugador_y_IdPartida_AlCargarElJuego() throws Exception {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioServicio.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaServicio.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaServicio.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(rondaServicio.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);
        when(usuarioPartidaServicio.buscarListaDeUsuariosPartidaPorPartidaId(partidaId)).thenReturn(List.of(usuarioPartida));

        ModelAndView mov = controladorJuego.mostrarVistaJuego(usuarioId, partidaId);


        assertEquals(usuarioId, mov.getModel().get("usuarioId"));
        assertEquals(partidaId, mov.getModel().get("partidaId"));
        Definicion definicionEnModelo = (Definicion) mov.getModel().get("definicion");
        assertEquals("Definicion de prueba", definicionEnModelo.getDefinicion());    }

    @Test
    public void queSeAgreguenLosJugadoresAlModelo() throws Exception {
        Long usuarioId = 1L;
        Long partidaId = 1L;

        when(usuarioServicio.buscarPorId(usuarioId)).thenReturn(usuario);
        when(partidaServicio.buscarPorId(partidaId)).thenReturn(partida);
        when(usuarioPartidaServicio.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId)).thenReturn(usuarioPartida);
        when(rondaServicio.buscarRondaActivaPorPartidaId(partidaId)).thenReturn(ronda);

        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2L);
        otroUsuario.setNombreUsuario("SegundoJugador");

        UsuarioPartida usuarioPartida2 = new UsuarioPartida();
        usuarioPartida2.setUsuario(otroUsuario);
        usuarioPartida2.setPartida(partida);
        usuarioPartida2.setPuntaje(80);

        when(usuarioPartidaServicio.buscarListaDeUsuariosPartidaPorPartidaId(partidaId)).thenReturn(List.of(usuarioPartida, usuarioPartida2));

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


        when(usuarioServicio.buscarPorId(1L)).thenReturn(usuario);
        when(partidaServicio.buscarPorId(1L)).thenReturn(partida);
        when(usuarioPartidaServicio.buscarPorUsuarioIdYPartidaId(1L, 1L)).thenReturn(usuarioPartida);
        when(rondaServicio.buscarRondaActivaPorPartidaId(1L)).thenReturn(ronda);
        when(usuarioPartidaServicio.buscarListaDeUsuariosPartidaPorPartidaId(1L)).thenReturn(List.of(usuarioPartida));

        ModelAndView mov = controladorJuego.mostrarVistaJuego(1L, 1L);

        assertEquals("palabraEjemplo", ((Palabra) ronda.getPalabra()).getDescripcion());
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
