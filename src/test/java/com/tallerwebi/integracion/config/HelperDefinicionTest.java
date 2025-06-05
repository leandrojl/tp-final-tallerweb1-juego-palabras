package com.tallerwebi.integracion.config;

import com.tallerwebi.helpers.HelperDefinicion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HelperDefinicionTest {

    @Test
            public void obtenerDefinicion() {
        HelperDefinicion hd = new HelperDefinicion();
        String definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("computadora", "Castellano");
        System.out.printf(definicion);
        assertNotNull(definicion);
    }
    @Test
    public void obtenerDefinicionDePalabraEnCastellano(){
        HelperDefinicion hd = new HelperDefinicion();
        String definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("computadora", "Castellano");
        System.out.printf(definicion);
        assertNotNull(definicion);
    }
    @Test
    public void obtenerDefinicionesDePalabraEnIngles(){
        HelperDefinicion hd = new HelperDefinicion();
        String definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("dog", "Ingles");
        System.out.printf(definicion);
        assertNotNull(definicion);
    }
}
