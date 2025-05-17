package com.tallerwebi.presentacion;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class ControladorVistaFinalTest {

    public final String nombre = "Pepito";
    public final Integer puntos = 500;

    @Test
    public void queAlCargarVistaFinalSeDirijaALaMisma() {
        //preparacion
        ControladorVistaFinal cvf = new ControladorVistaFinal();
        //ejecucion
        ModelAndView mav = cvf.verPantallaFinal(nombre, puntos);
        //validacion
        assertThat(mav.getViewName(), equalToIgnoringCase("vistaFinalJuego"));
    }

    @Test
    public void seCargaElNombreQueLePaso() {
        //preparacion
        ControladorVistaFinal cvf = new ControladorVistaFinal();
        //ejecucion
        ModelAndView mav = cvf.verPantallaFinal(nombre, puntos);
        //validacion
        assertThat(mav.getModel().get("nombre").toString(), equalToIgnoringCase("Pepito"));
    }

    @Test
    public void seCarganLosPuntosQueLePaso() {
        //preparacion
        ControladorVistaFinal cvf = new ControladorVistaFinal();
        //ejecucion
        ModelAndView mav = cvf.verPantallaFinal(nombre, puntos);
        //validacion
        assertThat((Integer) mav.getModel().get("puntos"), equalTo(500));
    }
}
