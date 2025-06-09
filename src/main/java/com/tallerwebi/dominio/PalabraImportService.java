package com.tallerwebi.dominio;

import com.tallerwebi.helpers.HelperPalabra;
import com.tallerwebi.dominio.PalabraRepository;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Definicion;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PalabraImportService {

    private final PalabraRepositoryImpl palabraRepository;

    public PalabraImportService(PalabraRepository palabraRepository) {
        this.palabraRepository = (PalabraRepositoryImpl) palabraRepository;
    }

    public void importarPalabraDesdeAPI(String idioma) {
        HelperPalabra helper = new HelperPalabra();

        Map<String, List<String>> palabraYDescripcion = helper.getPalabraYDescripcion(idioma);
        for (Map.Entry<String, List<String>> entry : palabraYDescripcion.entrySet()) {
            Palabra palabra = new Palabra();
            palabra.setDescripcion(entry.getKey());
            palabra.setIdioma(idioma.equalsIgnoreCase("Castellano") ? "es" : "en");

            List<Definicion> definiciones = new ArrayList<>();
            for (String def : entry.getValue()) {
                definiciones.add(new Definicion(def));
            }
            palabra.setDefinicion(definiciones);

            palabraRepository.guardar(palabra);
        }
    }
}
