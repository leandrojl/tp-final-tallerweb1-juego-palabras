package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.infraestructura.RondaServicioImpl;
import com.tallerwebi.presentacion.ControladorJuego;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class RondaServicioTest {
    RondaServicioImpl rondaServicio = new RondaServicioImpl();




    @Test
    void queRondaMeTraigaUnaDefinicion() {
        HashMap<String,String> pYD = rondaServicio.traerPalabraYDefinicion(); ;
        assertFalse(pYD.isEmpty());
    }


}
