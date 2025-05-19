package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.infraestructura.RondaServicioImpl;
import com.tallerwebi.presentacion.ControladorJuego;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class RondaServicioTest {
    RondaServicioImpl rondaServicio = new RondaServicioImpl();

    private ControladorJuego controladorJuego;



    @Test
    void testAvanzarRonda() {

        boolean resultado = rondaServicio.avanzarRonda();
        assertTrue(resultado);
        assertEquals(2, rondaServicio.obtenerNumeroRonda());
    }


    @Test
    void queRondaMeTraigaUnaDefinicion() {
        HashMap<String,String> definicion = rondaServicio.traerPalabraYDefinicion(); ;

        assertEquals(true, definicion.containsKey("example"));
    }


}
