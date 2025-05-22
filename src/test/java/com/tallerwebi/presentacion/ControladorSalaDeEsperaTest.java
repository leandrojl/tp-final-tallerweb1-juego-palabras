package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.NoHayJugadoresEnLaSalaDeEsperaException;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class ControladorSalaDeEsperaTest {

    ServicioSalaDeEspera servicioSalaDeEspera = mock(ServicioSalaDeEspera.class);
    ControladorSalaDeEspera controlador = new ControladorSalaDeEspera(servicioSalaDeEspera);

    @Test
    public void deberiaMostrarIrALaVistaJuegoCuandoLosJugadoresEstanListos() {

        //dado que tengo los jugadores que vienen del formulario en la sala de espera
        Map<String, String> parametros = Map.of("jugador_1", "false", "jugador_2", "false");

        Map<Long, Boolean> jugadores = Map.of(1L, true, 2L, true);
        List<Long> jugadoresNoListos = List.of();

        when(servicioSalaDeEspera.obtenerJugadoresDelFormulario(parametros)).thenReturn(jugadores);

        when(servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores)).thenReturn(jugadoresNoListos);

        ModelAndView mav = controlador.iniciarPartida(parametros);

        assertThat(mav.getViewName(), equalTo("redirect:/juego?jugadorId=1"));

        verify(servicioSalaDeEspera).obtenerJugadoresDelFormulario(parametros);
        verify(servicioSalaDeEspera).verificarSiHayJugadoresQueNoEstenListos(jugadores);

    }

    @Test
    public void deberiaMostrarErrorCuandoLosJugadoresNoEstanListos() {

        //dado que tengo los jugadores que vienen del formulario en la sala de espera
        Map<String, String> parametros = Map.of("jugador_1", "false", "jugador_2", "false");

        Map<Long, Boolean> jugadores = Map.of(1L, false, 2L, false);
        List<Long> jugadoresNoListos = List.of(1L, 2L);

        when(servicioSalaDeEspera.obtenerJugadoresDelFormulario(parametros)).thenReturn(jugadores);

        when(servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores)).thenReturn(jugadoresNoListos);

        ModelAndView mav = controlador.iniciarPartida(parametros);

        assertThat(mav.getViewName(), equalTo("sala-de-espera"));
        ModelMap model = mav.getModelMap(); //guardo el modelo que va a la vista desde el controlador
        assertThat(model.get("error"), equalTo("Los siguientes jugadores no están listos: [1, 2]"));

        verify(servicioSalaDeEspera).obtenerJugadoresDelFormulario(parametros);
        verify(servicioSalaDeEspera).verificarSiHayJugadoresQueNoEstenListos(jugadores);

    }

    @Test
    public void cuandoAgregoUnJugadorALaSalaDeEsperaLoRedirijaALaSalaDeEspera() {

        Jugador jugador = dadoQueTengoUnJugadorConNombre("Messi");

        ModelAndView mavJugador = cuandoAgregoAUnJugadorALaSalaDeEspera(jugador);

        entoncesRedirigoAlJugadorALaSalaDeEspera(mavJugador);

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
    public void dadoQueTengoUnaSalaDeEspera(){
        SalaDeEspera salaDeEspera = new SalaDeEspera();

        assertThat(salaDeEspera, notNullValue());
    }

    private void dadoQueNoTengoJugadoresEnLaSalaDeEspera() {
    }

    @Test
    public void siNoHayUnJugadorQueRetorneElControladorAlLobby(){

    }

    public Jugador dadoQueTengoUnJugadorConNombre(String nombre) {

        return new Jugador(nombre);
    }

    public ModelAndView cuandoAgregoAUnJugadorALaSalaDeEspera(Jugador jugador) {
        ControladorSalaDeEspera controladorSalaDeEspera = new ControladorSalaDeEspera(servicioSalaDeEspera);
        ModelAndView mav = controladorSalaDeEspera.agregarJugadorALaSalaDeEspera(jugador);
        return mav;
    }

    public void entoncesRedirigoAlJugadorALaSalaDeEspera(ModelAndView mav) {
        assertThat(mav.getViewName(),equalToIgnoringCase("sala-de-espera"));
    }
}
