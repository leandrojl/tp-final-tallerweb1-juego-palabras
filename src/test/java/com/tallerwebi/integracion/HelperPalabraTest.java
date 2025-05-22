package com.tallerwebi.integracion;

import com.tallerwebi.HelperPalabra;

import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HelperPalabraTest {

@Test
    public void obtenerUnMap(){
    HelperPalabra helperPalabra = new HelperPalabra ();
    Map<String, List<String>> palabra = helperPalabra.getPalabraYDescripcion("Castellano");
    assertThat(palabra, notNullValue());
}
@Test
    public void obtenerUnMapConUnaPalabra(){
    HelperPalabra helperPalabra = new HelperPalabra ();
    Map<String,List<String>> palabra = helperPalabra.getPalabraYDescripcion("Castellano");
    assertThat(palabra, is(not(emptyMap())));
}
@Test
    public void obtenerUnStringDeArchivoTxt(){
    HelperPalabra helperPalabra = new HelperPalabra ();
    String palabra = helperPalabra.getPalabra("Castellano");
     assertThat(palabra, is(not("")));
}
@Test
    public void buscarDescripcionEnHelperDefinicionEnCastellano(){
    HelperPalabra helperPalabra = new HelperPalabra ();
    List<String> definicion = helperPalabra.getDefinicion("perro", "Castellano");
    assertNotNull(definicion);
}
@Test
public void buscarDescripcionEnHelperDefinicionEnIngles(){
    HelperPalabra helperPalabra = new HelperPalabra ();
    List<String> definicion = helperPalabra.getDefinicion("perro", "Ingles");
    assertNotNull(definicion);
}
@Test
public void ObtenerUnaPalabraEnCastellanoYBuscarlaEnDefiniciones(){
    HelperPalabra helperPalabra = new HelperPalabra ();
    Map<String,List<String>> palabra = helperPalabra.getPalabraYDescripcion("Castellano");

    for (Map.Entry<String, List<String>> entry : palabra.entrySet()) {
        
        assertThat(entry.getKey(), is(notNullValue()));
        assertThat(entry.getValue(), is(notNullValue()));
    }
}
@Test
    public void ObetenerUnaPalabraEnInglesYBuscarlaEnDefiniciones(){
    HelperPalabra helperPalabra = new HelperPalabra ();
    Map<String,List<String>> palabra = helperPalabra.getPalabraYDescripcion("Ingles");
    for (Map.Entry<String, List<String>> entry : palabra.entrySet()) {
        assertThat(entry.getKey(), is(notNullValue()));
        assertThat(entry.getValue(), is(notNullValue()));
    }
}

}
