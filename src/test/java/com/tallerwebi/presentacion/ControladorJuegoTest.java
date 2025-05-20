package com.tallerwebi.presentacion;

import com.tallerwebi.infraestructura.PartidaServicio;
import com.tallerwebi.dominio.RondaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Mockito.*;

public class ControladorJuegoTest {

    private RondaServicio rondaServicio;
    private PartidaServicio partidaMock;
    private ControladorJuego controladorJuego;

    @BeforeEach
    public void init() {
        rondaServicio = mock(RondaServicio.class);
        partidaMock = mock(PartidaServicio.class);
        controladorJuego = new ControladorJuego(rondaServicio, partidaMock);
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
        thenSeVeLaPaginaJuego(mov, "juego");
    }

    private void thenSeVeLaPaginaJuego(ModelAndView mov, String juego) {
        assertThat(mov.getViewName(), equalToIgnoringCase(juego));
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
        //preparacion
        String idJugador = "1";
        //simulo la respuesta del servicio ya que es una dependencia
        when(rondaServicio.traerPalabraYDefinicion()).thenReturn(getPalabraYDefinicion());

        //ejecucion
        ModelAndView mov = whenObtenerVistaJuego(idJugador);

        //validacion
        assertThat((String) mov.getModel().get("jugadorId"), equalToIgnoringCase(idJugador));
    }


    @Test
    public void queSeAgregueElJugadorALaPartida() {
        //preparacion
        String idJugador = "1";
        //simulo la respuesta del servicio ya que es una dependencia
        when(rondaServicio.traerPalabraYDefinicion()).thenReturn(getPalabraYDefinicion());
        //ejecucion
        ModelAndView mov = whenObtenerVistaJuego(idJugador);

        //validacion - verify comprueba que un metodo de partidaMock fue llamado con ciertos argumentos
        verify(partidaMock).agregarJugador(idJugador);
    }

    @Test
    public void siLaPalabraActualEsNullSeInvoqueAlServicioParaTraerPalabraYDefinicion() {
        // preparación
        String idJugador = "1";
        when(partidaMock.getPalabraActual()).thenReturn(null).thenReturn("agua");
        when(partidaMock.getDefinicionActual()).thenReturn("Sustancia líquida esencial para la vida.");
        when(partidaMock.getRondaActual()).thenReturn(1);
        when(rondaServicio.traerPalabraYDefinicion()).thenReturn(getPalabraYDefinicion());

        // ejecución
        ModelAndView mov = whenObtenerVistaJuego(idJugador);

        // validación
        verify(partidaMock).agregarJugador(idJugador);
        verify(rondaServicio).traerPalabraYDefinicion();
        verify(partidaMock).actualizarPuntos(idJugador, 0);
        verify(partidaMock).avanzarRonda("agua", "Sustancia líquida esencial para la vida.");

        assertThat(mov.getViewName(), equalToIgnoringCase("juego"));
        assertThat(mov.getModel().get("jugadorId").toString(), equalToIgnoringCase(idJugador));
        assertThat(mov.getModel().get("definicion").toString(), equalToIgnoringCase("Sustancia líquida esencial para la vida."));
        assertThat(mov.getModel().get("palabra").toString(), equalToIgnoringCase("agua"));
        assertThat(mov.getModel().get("rondaActual").toString(), equalToIgnoringCase("1"));
    }

    @Test
    public void siYaHayUnaPalabraNoSeLlamaAlServicioNiSeAvanzaRonda() {
        // preparacion
        String idJugador = "2";
        //simulamos que la partida ya está activa con esto y el controlador deberia saltear el if
        when(partidaMock.getPalabraActual()).thenReturn("fuego");
        when(partidaMock.getDefinicionActual()).thenReturn("Elemento que quema.");
        when(partidaMock.getRondaActual()).thenReturn(2);

        ModelAndView mov = whenObtenerVistaJuego(idJugador);

        //validacion
        //aun cuando hay una palabra se debe agregar el idJugador
        verify(partidaMock).agregarJugador(idJugador);
        //verifico que no se avanzo de ronda
        verify(partidaMock, never()).avanzarRonda(anyString(), anyString());
        //verifico que no se llamo al servicio
        verify(rondaServicio, never()).traerPalabraYDefinicion();

        //valido que el mov tenga la info esperada de la partida activa
        assertThat(mov.getModel().get("palabra").toString(), equalToIgnoringCase("fuego"));
        assertThat(mov.getModel().get("definicion").toString(), equalToIgnoringCase("Elemento que quema."));
        assertThat(mov.getModel().get("rondaActual").toString(), equalToIgnoringCase("2"));

    }


    //------------------------Tests de PROCESAR INTENTO AJAX---------------------------------------//

    @Test
    public void queSeRecibaElIntentoCorrectoYSeAvanceRonda() {
        String idJugador = "1";
        String intento = "example";

        // preparo el estado inicial
        when(partidaMock.getPalabraActual()).thenReturn("example"); // palabra actual correcta
        when(partidaMock.isPartidaTerminada()).thenReturn(false);   // aún no termina
        when(rondaServicio.traerPalabraYDefinicion()).thenReturn(getPalabraYDefinicion());
        when(partidaMock.avanzarRonda(anyString(), anyString())).thenReturn(true);
        when(partidaMock.getRondaActual()).thenReturn(2);
        when(partidaMock.getPuntaje(idJugador)).thenReturn(1);

        // ejecuto el metodo
        Map<String, Object> resultado = controladorJuego.procesarIntentoAjax(intento, idJugador);

        // verifico comportamiento
        verify(partidaMock).actualizarPuntos(idJugador, 1);
        verify(partidaMock).avanzarRonda("agua", "Sustancia líquida esencial para la vida.");
        verify(rondaServicio).traerPalabraYDefinicion();

        // verifico respuesta
        assertThat(resultado.get("correcto").toString(), equalToIgnoringCase("true"));
        assertThat(resultado.get("partidaTerminada").toString(), equalToIgnoringCase("false"));
        assertThat(resultado.get("nuevaPalabra").toString(), equalToIgnoringCase("agua"));
        assertThat(resultado.get("nuevaDefinicion").toString(), equalToIgnoringCase("Sustancia líquida esencial para la vida."));
        assertThat(resultado.get("ronda").toString(), equalToIgnoringCase("2"));
        assertThat(resultado.get("puntaje").toString(), equalToIgnoringCase("1"));
    }

    @Test
    public void queSeRecibaElIntentoIncorrectoYNoSeAvanceDeRonda() {

        // Preparacion
        String idJugador = "1";
        String intento = "other";

        when(partidaMock.getPalabraActual()).thenReturn("example"); // palabra actual correcta
        when(partidaMock.isPartidaTerminada()).thenReturn(false);   // aún no termina
        when(partidaMock.getRondaActual()).thenReturn(1);
        when(partidaMock.getPuntaje(idJugador)).thenReturn(0);

        // Ejecuto el metodo
        Map<String, Object> resultado = controladorJuego.procesarIntentoAjax(intento, idJugador);

        // Verifico comportamiento
        verify(partidaMock, never()).actualizarPuntos(anyString(), anyInt());
        verify(partidaMock, never()).avanzarRonda(anyString(), anyString());
        verify(rondaServicio, never()).traerPalabraYDefinicion();

        assertThat(resultado.get("correcto").toString(), equalToIgnoringCase("false"));
        assertThat(resultado.get("ronda").toString(), equalToIgnoringCase("1"));
        assertThat(resultado.get("puntaje").toString(), equalToIgnoringCase("0"));

    }

    @Test
    public void queNoSeAvanceSiLaPartidaYaTermino() {
        // Preparacion
        String idJugador = "1";
        String intento = "other";

        when(partidaMock.getPalabraActual()).thenReturn("other");
        when(partidaMock.isPartidaTerminada()).thenReturn(true);
        when(partidaMock.getPuntaje(idJugador)).thenReturn(20);
        when(partidaMock.getRondaActual()).thenReturn(5);

        //ejecutar el metodo
        Map<String, Object> resultado = controladorJuego.procesarIntentoAjax(intento, idJugador);

        //validacion
        verify(partidaMock).actualizarPuntos(idJugador, 1);
        verify(partidaMock, never()).avanzarRonda(anyString(), anyString());
        verify(rondaServicio, never()).traerPalabraYDefinicion();

        assertThat(resultado.get("partidaTerminada").toString(), equalToIgnoringCase("true"));

    }

    //------------------------Tests de FIN RONDA---------------------------------------//

}