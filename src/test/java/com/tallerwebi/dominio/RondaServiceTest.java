package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

class RondaServiceTest {
    RondaServiceImpl rondaServicio = new RondaServiceImpl();




    @Test
    void queRondaMeTraigaUnaDefinicion() {
        HashMap<String,String> pYD = rondaServicio.traerPalabraYDefinicion(); ;
        assertFalse(pYD.isEmpty());
    }


}
