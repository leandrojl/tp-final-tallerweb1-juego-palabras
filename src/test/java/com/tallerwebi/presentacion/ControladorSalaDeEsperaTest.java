package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.SalaDeEspera;
import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.servlet.ModelAndView;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ControladorSalaDeEsperaTest {


    @Test
    public void cuandoJugador1CambiaEstadoAListoDesdeElBotonDeSalaDeEsperaDebeActualizarseElEstadoEnLaSalaDeEspera() {
    /*
        //dado que tengo un controlador sala de espera
        ControladorSalaDeEspera controlador = new ControladorSalaDeEspera();
        //dado que tengo una sala de espera
        SalaDeEspera salaDeEspera = new SalaDeEspera();


        controlador.setSalaDeEspera(salaDeEspera);

        salaDeEspera.setJugador1(new Usuario("Messi", 1L));
        salaDeEspera.setJugador2(new Usuario("Ronaldo", 2L));

        // Ejecutar el método del controlador

        controlador.toggleReady(1L, true);

        // Verificar que el estado del jugador 1 se actualizó
        assertTrue(salaDeEspera.isJugador1Listo());*/
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

    private void dadoQueNoTengoJugadoresEnLaSalaDeEspera() {
    }

    @Test
    public void siNoHayUnJugadorQueRetorneElControladorAlLobby(){

    }

    public Jugador dadoQueTengoUnJugadorConNombre(String nombre) {

        return new Jugador(nombre);
    }

    public ModelAndView cuandoAgregoAUnJugadorALaSalaDeEspera(Jugador jugador) {
        ControladorSalaDeEspera controladorSalaDeEspera = new ControladorSalaDeEspera();
        ModelAndView mav = controladorSalaDeEspera.agregarJugadorALaSalaDeEspera(jugador);
        return mav;
    }

    public void entoncesRedirigoAlJugadorALaSalaDeEspera(ModelAndView mav) {
        assertThat(mav.getViewName(),equalToIgnoringCase("sala-de-espera"));
    }
}
