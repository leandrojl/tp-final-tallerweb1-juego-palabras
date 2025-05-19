package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Partida;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ControladorJuegoTest {

    Partida partidaMock = mock(Partida.class);

    @Test
    public void queSeMuestreLaVistaJuego(){
        //preparacion

        //ejecucion
        ModelAndView mov = mostrarVistaJuego("1");

        //validacion
        assertThat(mov.getViewName(), equalToIgnoringCase("juego"));
    }

    @Test
    public void queSeRecibaElIdJugadorAlCargarElJuego(){
        //preparacion
        String idJugador = "1";

        //ejecucion
        ModelAndView mov = mostrarVistaJuego(idJugador);

        //validacion
        assertThat(mov.getViewName(),equalToIgnoringCase("juego"));
        assertThat((String) mov.getModel().get("idJugador"), equalToIgnoringCase(idJugador));
    }

    private ModelAndView mostrarVistaJuego(String idJugador) {
        ControladorJuego controladorJuego = new ControladorJuego(partidaMock);
        ModelAndView mov = controladorJuego.mostrarVistaDeJuego(idJugador);
        return mov;
    }

    @Test
    public void queSeAgregueElJugadorALaPartida(){
        //preparacion
        String idJugador = "1";

        //ejecucion
        ModelAndView mov = mostrarVistaJuego(idJugador);

        //validacion
        verify(partidaMock).agregarJugador(idJugador);

    }

    @Test
    public void queSeEnvieLaDefinicionDePalabraActual(){
        //preparacion
        String idJugador = "1";

        //ejecucion
        ModelAndView mov = mostrarVistaJuego(idJugador);

        //validacion
        assertThat((String) mov.getModel().get("definicionPalabraActual"), equalToIgnoringCase("A sample word for demonstration purposes."));
    }

    //Test de intento con ajax

    @Test
    public void queSeRecibaElIntentoAlIntentarAcertar(){
        //preparacion
        ControladorJuego controladorJuego = new ControladorJuego(partidaMock);

        String intento = "example";
        String idJugador = "1";
        Map<String, Object> resultado = controladorJuego.procesarIntentoConAjax(intento, idJugador);

        assertThat(resultado.get("correcto").toString(), equalToIgnoringCase("true"));
    }

}
