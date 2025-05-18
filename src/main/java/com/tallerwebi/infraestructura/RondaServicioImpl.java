package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RondaServicio;
import org.springframework.stereotype.Service;

@Service
public class RondaServicioImpl implements RondaServicio {

    private final int MAX_RONDAS = 5;
    private int rondaActual = 1;

    @Override
    public int obtenerNumeroRonda() {
        return rondaActual;
    }

    @Override
    public boolean avanzarRonda() {
        if (rondaActual < MAX_RONDAS) {
            rondaActual++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void reiniciarRonda() {
        rondaActual = 1;
    }


}

