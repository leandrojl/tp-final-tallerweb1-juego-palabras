package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.model.Jugador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JuegoControllerTest {

    private PuntajeService puntajeServicio;
    private PartidaService partidaServicio;
    private RondaService rondaServicio;
    private JuegoController controladorJuego;

    @BeforeEach
    public void setUp() {
        rondaServicio = mock(RondaService.class);
        puntajeServicio = new PuntajeServiceImpl();
        partidaServicio = new PartidaServiceImpl();
        controladorJuego = new JuegoController(rondaServicio, puntajeServicio, partidaServicio);
    }

    @Test
    public void queSeMuestreLaVistaJuego() {
        ModelAndView mov = controladorJuego.mostrarVistaJuego("1");
        assertThat(mov.getViewName(), equalToIgnoringCase("juego"));
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
        assertEquals("Jugador_1", partidaServicio.obtenerPartida(idJugador).getNombre(idJugador));
    }

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
    }
}
