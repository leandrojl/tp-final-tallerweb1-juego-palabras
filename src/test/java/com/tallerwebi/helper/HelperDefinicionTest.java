package com.tallerwebi.helper;

import com.tallerwebi.helpers.HelperDefinicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HelperDefinicionTest {

    @Test
    public void obtenerDefinicion() {
        HelperDefinicion hd = new HelperDefinicion();
        List<String> definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("computadora", "Castellano");
        assertNotNull(definicion);
    }
    @Test
    public void obtenerDefinicionDePalabraEnCastellano(){
        HelperDefinicion hd = new HelperDefinicion();
        List<String> definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("computadora", "Castellano");
        for(String e : definicion){
            System.out.printf(e);
        }
        assertNotNull(definicion);
    }
    @Test
    public void obtenerDefinicionesDePalabraEnIngles(){
        HelperDefinicion hd = new HelperDefinicion();
        List<String> definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("dog", "Ingles");
        assertNotNull(definicion);
    }/*
    @Test
    public void obtenerCodigoParaCastellano(){
        HelperDefinicion hd = new HelperDefinicion();
        String codigo = hd.getCodigoIdioma("Castellano");
        assertThat(codigo, equalTo("es"))    }
    @Test
    public void obtenerCodigoParaIngles(){
        HelperDefinicion hd = new HelperDefinicion();
        String codigo = hd.getCodigoIdioma("Ingles");
        assertThat(codigo, equalTo("en"))    }

        */
}