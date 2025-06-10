package com.tallerwebi.dominio;

import com.tallerwebi.helpers.HelperPalabra;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.helpers.IPalabraHelper;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
@Transactional
public class PalabraImportService {

    private final PalabraRepository palabraRepository;
    private final IPalabraHelper palabraHelper;

    public PalabraImportService(PalabraRepository palabraRepository, IPalabraHelper palabraHelper) {
        this.palabraRepository = palabraRepository;
        this.palabraHelper = palabraHelper;
    }

    public void importarPalabraDesdeAPI(String idioma) {
        Map<String, List<String>> palabras = palabraHelper.getPalabraYDescripcion(idioma);
        if (palabras == null) return;

        for (Map.Entry<String, List<String>> entry : palabras.entrySet()) {
            Palabra palabra = new Palabra();
            palabra.setDescripcion(entry.getKey());
            palabra.setIdioma(idioma.equalsIgnoreCase("Castellano") ? "es" : "en");
            List<Definicion> defs = new ArrayList<>();
            for (String def : entry.getValue()) {
                defs.add(new Definicion(def));
            }
            palabra.setDefinicion(defs);
            palabraRepository.guardar(palabra);
        }
    }
}

