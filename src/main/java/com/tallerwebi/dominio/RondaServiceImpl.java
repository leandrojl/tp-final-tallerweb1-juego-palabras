package com.tallerwebi.dominio;

import com.tallerwebi.helpers.HelperPalabra;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;

@Service
@Transactional
public class RondaServiceImpl implements RondaService {

    private final int MAX_RONDAS = 5;
    private int rondaActual = 1;
    private final HelperPalabra helperPalabra = new HelperPalabra();




    @Override
    public HashMap<String, String> traerPalabraYDefinicion() {
        // Aquí podés pasar el idioma elegido por el usuario, por ahora lo dejamos fijo:
        String idioma = "Castellano";
        return new HashMap<>(helperPalabra.getPalabraYDescripcion(idioma));
    }



}

