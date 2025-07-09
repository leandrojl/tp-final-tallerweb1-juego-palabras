package com.tallerwebi.helper;

import com.tallerwebi.helpers.HelperDefinicion;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HelperDefinicionTest {

    @Test
    public void obtenerDefinicionDesdeWikidataEnEspañol() {
        List<String> definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("computadora", "Español");
        assertNotNull(definicion);
        assertThat(definicion, is(not(empty())));
    }

    @Test
    public void obtenerDefinicionDesdeWikidataEnIngles() {
        List<String> definicion = HelperDefinicion.obtenerDescripcionDesdeWikidata("dog", "Ingles");
        assertNotNull(definicion);
        assertThat(definicion, is(not(empty())));
    }


}

