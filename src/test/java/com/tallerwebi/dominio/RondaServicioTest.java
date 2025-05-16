package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tallerwebi.infraestructura.RondaServicioImpl;
import org.junit.jupiter.api.Test;

class RondaServicioTest {

    @Test
    void testAvanzarRonda() {
        RondaServicio rondaServicio =  new RondaServicioImpl();
        rondaServicio.iniciarRonda();
        rondaServicio.avanzarRonda();
        assertEquals(2, rondaServicio.obtenerNumeroRonda());
    }
}
