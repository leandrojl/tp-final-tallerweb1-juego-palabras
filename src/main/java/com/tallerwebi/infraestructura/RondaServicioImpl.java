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
    public HashMap<String, String> traerPalabraYDefinicion() {
        // Aquí podés pasar el idioma elegido por el usuario, por ahora lo dejamos fijo:
        String idioma = "Castellano";
       // return new HashMap<String,String>(helperPalabra.getPalabraYDescripcion(idioma));
        return null;
    }



}

