package com.tallerwebi.dominio;


import java.util.HashMap;

public interface RondaServicio {
    int obtenerNumeroRonda();
    boolean avanzarRonda();
    void reiniciarRonda();

    HashMap<String, String> traerPalabraYDefinicion();
}
