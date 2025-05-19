package com.tallerwebi.infraestructura;

import com.tallerwebi.HelperPalabra;
import com.tallerwebi.dominio.RondaServicio;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;

@Service
@Transactional
public class RondaServicioImpl implements RondaServicio {

    private final int MAX_RONDAS = 5;
    private int rondaActual = 1;
    private final HelperPalabra helperPalabra = new HelperPalabra();


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

    @Override
    public HashMap<String, String> traerPalabraYDefinicion() {
        // Aquí podés pasar el idioma elegido por el usuario, por ahora lo dejamos fijo:
        String idioma = "Castellano";
        return new HashMap<>(helperPalabra.getPalabraYDescripcion(idioma));
    }



}

