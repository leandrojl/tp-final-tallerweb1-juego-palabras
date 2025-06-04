package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControladorJuegoTest {

    private PuntajeServicio puntajeServicio;
    private RondaServicio rondaServicio;
    private PartidaServicio partidaMock;
    private PuntajeServicio puntajeMock;
    private ControladorJuego controladorJuego;

    @BeforeEach
    public void setUp() {
        rondaServicio = mock(RondaServicio.class);
        partidaMock = mock(PartidaServicio.class);
        puntajeMock = mock(PuntajeServicio.class);
        controladorJuego = new ControladorJuego(rondaServicio, puntajeMock, partidaMock);
    }

    @Test
    public void queSeMuestreLaVistaJuego() {
        //preparacion
        String idUsuario = "1";
        //simulo la respuesta del servicio ya que es una dependencia
        when(rondaServicio.traerPalabraYDefinicion()).thenReturn(getPalabraYDefinicion());

        //ejecucion
        ModelAndView mov = whenObtenerVistaJuego(idUsuario);

        //validacion
        thenSeVeLaPaginaJuego(mov);
    }

    private void thenSeVeLaPaginaJuego(ModelAndView mov) {
        assertThat(mov.getViewName(), equalToIgnoringCase("juego"));
    }

    private ModelAndView whenObtenerVistaJuego(String idJugador) {
        return controladorJuego.mostrarVistaJuego(idJugador);
    }

    private HashMap<String, String> getPalabraYDefinicion() {
        HashMap<String, String> palabraYDefinicion = new HashMap<>();
        palabraYDefinicion.put("agua", "Sustancia líquida esencial para la vida.");
        return palabraYDefinicion;
    }

    @Test
    public void queSeRecibaElIdJugadorAlCargarElJuego() {
        String idJugador = "1";
        ModelAndView mov = controladorJuego.mostrarVistaJuego(idJugador);
        assertThat(mov.getViewName(), equalToIgnoringCase("juego"));
        assertThat((String) mov.getModel().get("jugadorId"), equalToIgnoringCase(idJugador));
    }

    @Test
    public void queSeAgregueElJugadorALaPartida() {
        String idJugador = "1";
        controladorJuego.mostrarVistaJuego(idJugador);
        assertEquals("Jugador_1", partidaMock.obtenerPartida(idJugador).getNombre(idJugador));
    }

    @Test
    public void queSeRecibaElIntentoAlIntentarAcertar() {
        String intento = "example";
        String idJugador = "1";
        int tiempoRestante = 50;

        partidaMock.iniciarNuevaPartida(idJugador, "Gian");
        partidaMock.obtenerPartida(idJugador).avanzarRonda("example", "Definicion ejemplo");

        Map<String, Object> resultado = controladorJuego.procesarIntentoAjax(intento, idJugador, tiempoRestante);

        assertEquals(true, resultado.get("correcto"));
        assertEquals(100, resultado.get("puntaje"));
    }

    @Test
    public void queFinaliceLaPartidaAlLlegarALaUltimaRonda() {
        String jugadorId = "1";
        partidaMock.iniciarNuevaPartida(jugadorId, "july3p");

        for (int i = 0; i < 5; i++) {
            partidaMock.obtenerPartida(jugadorId).avanzarRonda("palabra" + i, "definición" + i);
        }

        assertTrue(partidaMock.obtenerPartida(jugadorId).isPartidaTerminada());
    }

    @Test
    public void queSeMuestreVistaFinalConRankingYGanador() {
        String jugadorId = "1";
        String nombre = "July3p";

        Jugador jugador = new Jugador(jugadorId, nombre, "july3p@hotmail.com", "pass");
        partidaMock.iniciarNuevaPartida(jugadorId, nombre);
        puntajeServicio.registrarJugador(jugadorId, jugador);
        puntajeServicio.registrarPuntos(jugadorId, 500);

        Model model = new ExtendedModelMap();
        String viewName = controladorJuego.mostrarVistaFinal(jugadorId, model);

        assertEquals("vistaFinalJuego", viewName);
        assertNotNull(model.getAttribute("ranking"));
        assertEquals(nombre, model.getAttribute("ganador"));
        assertEquals(nombre, model.getAttribute("jugadorActual"));
    }
}
