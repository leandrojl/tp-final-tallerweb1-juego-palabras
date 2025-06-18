package com.tallerwebi.dominio;


import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.helpers.HelperPalabra;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional
public class RondaServiceImpl implements RondaService {

    private final int MAX_RONDAS = 5;
    private int rondaActual = 1;
    private final HelperPalabra helperPalabra = new HelperPalabra();




    @Override
    public HashMap<String, String> traerPalabraYDefinicion() {
        String idioma = "Castellano";
        HashMap<String, List<String>> originalMap = (HashMap<String, List<String>>) helperPalabra.getPalabraYDescripcion(idioma);
        HashMap<String, String> transformedMap = new HashMap<>();

        for (String key : originalMap.keySet()) {
            List<String> values = originalMap.get(key);
            if (values != null && !values.isEmpty()) {
                transformedMap.put(key, values.get(0)); // Tomar el primer elemento de la lista
            }
        }

        return transformedMap;
    }


}

