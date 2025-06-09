package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class PalabraServicioImpl implements PalabraService {

    private final PalabraRepository palabraRepository;

    @Autowired
    public PalabraServicioImpl(PalabraRepository palabraRepository) {
        this.palabraRepository = palabraRepository;
    }

    @Override
    public HashMap<Palabra, List<Definicion>> traerPalabraYDefinicion(String idioma) {
        List<Palabra> palabras;

        switch (idioma) {
            case "Mixto":
                palabras = palabraRepository.buscarTodas(); break;
            case "Castellano":
                palabras = palabraRepository.buscarPorIdioma("es"); break;
            default:
                palabras = palabraRepository.buscarPorIdioma("en"); break;
        }

        if (palabras.isEmpty()) throw new RuntimeException("No hay palabras disponibles");

        Palabra seleccionada = palabras.get(new Random().nextInt(palabras.size()));

        HashMap<Palabra, List<Definicion>> resultado = new HashMap<>();
        resultado.put(seleccionada, seleccionada.getDefinicion());
        return resultado;
    }
}



