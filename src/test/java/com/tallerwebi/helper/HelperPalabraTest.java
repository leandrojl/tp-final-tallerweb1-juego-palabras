package com.tallerwebi.helper;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.helpers.HelperPalabra;

import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HelperPalabraTest {

    @Test
    public void obtenerUnMap() {
        HelperPalabra helperPalabra = new HelperPalabra();
        Map<Palabra, List<Definicion>> palabra = helperPalabra.getPalabraYDescripcion("Español");
        assertThat(palabra, is(notNullValue()));
    }

    @Test
    public void obtenerUnMapConUnaPalabra() {
        HelperPalabra helperPalabra = new HelperPalabra();
        Map<Palabra, List<Definicion>> palabra = helperPalabra.getPalabraYDescripcion("Español");
        assertThat(palabra, is(not(anEmptyMap())));
    }

    @Test
    public void obtenerUnStringDeArchivoTxt() {
        HelperPalabra helperPalabra = new HelperPalabra();
        String palabra = helperPalabra.obtenerPalabraDesdeQidAleatorio("Ingles");
        assertThat(palabra, is(not(emptyOrNullString())));
    }

    @Test
    public void buscarDescripcionEnHelperDefinicionEnCastellano() {
        HelperPalabra helperPalabra = new HelperPalabra();
        List<String> definicion = helperPalabra.getDefinicion("perro", "Español");
        assertNotNull(definicion);
    }

    @Test
    public void buscarDescripcionEnHelperDefinicionEnIngles() {
        HelperPalabra helperPalabra = new HelperPalabra();
        List<String> definicion = helperPalabra.getDefinicion("dog", "Ingles");
        assertNotNull(definicion);
    }

    @Test
    public void obtenerUnaPalabraEnCastellanoYBuscarlaEnDefiniciones() {
        HelperPalabra helperPalabra = new HelperPalabra();
        Map<Palabra, List<Definicion>> mapa = helperPalabra.getPalabraYDescripcion("Español");

        for (Map.Entry<Palabra, List<Definicion>> entry : mapa.entrySet()) {
            System.out.println(entry.getKey().getDescripcion() + ": " + entry.getValue());
            assertThat(entry.getKey().getDescripcion(), notNullValue());
            assertThat(entry.getValue(), is(not(empty())));
        }
    }

    @Test
    public void obtenerUnaPalabraEnInglesYBuscarlaEnDefiniciones() {
        HelperPalabra helperPalabra = new HelperPalabra();
        Map<Palabra, List<Definicion>> mapa = helperPalabra.getPalabraYDescripcion("Ingles");

        for (Map.Entry<Palabra, List<Definicion>> entry : mapa.entrySet()) {
            System.out.println(entry.getKey().getDescripcion() + ": " + entry.getValue());
            assertThat(entry.getKey().getDescripcion(), notNullValue());
            assertThat(entry.getValue(), is(not(empty())));
        }
    }
}
