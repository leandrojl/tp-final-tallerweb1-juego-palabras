package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RondaServicio;

public class RondaServicioImpl implements RondaServicio {

    private final int MAX_RONDAS = 5;
    private int numeroRonda = 1;


    @Override
    public void iniciarRonda() {

    }

    @Override
    public boolean avanzarRonda() {
        if (numeroRonda < MAX_RONDAS) {
            numeroRonda++;
            return true;
        } else {
            return false;
        }

    }

    @Override
    public int obtenerNumeroRonda() {
        return numeroRonda;

    }
}
