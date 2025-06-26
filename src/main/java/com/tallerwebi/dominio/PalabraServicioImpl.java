package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceService.PalabraServicio;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.helpers.HelperPalabra;
import com.tallerwebi.infraestructura.PalabraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class PalabraServicioImpl implements PalabraServicio {

    private final HelperPalabra helperPalabra = new HelperPalabra();

    @Autowired
    private PalabraRepository palabraRepositorio;

    @Override
    public Palabra obtenerPalabraAleatoriaPorIdioma(String idioma) {
        List<Palabra> palabras = palabraRepositorio.buscarPorIdioma(idioma);
        return obtenerAleatoria(palabras);
    }

    @Override
    public Palabra obtenerPalabraAleatoriaMixta() {
        List<Palabra> todas = palabraRepositorio.buscarTodas();
        return obtenerAleatoria(todas);
    }



    @Override
    public Palabra obtenerPalabraConDefinicionesDesdeHelper(String idioma) {
        // Usamos el nuevo helper que devuelve Map<Palabra, List<Definicion>>
        Map<Palabra, List<Definicion>> palabraYDefiniciones = helperPalabra.getPalabraYDescripcion(idioma);

        Palabra palabra = palabraYDefiniciones.keySet().iterator().next();

        // Verificamos si la palabra ya existe en la base
        Palabra palabraExistente = palabraRepositorio.buscarPorTexto(palabra.getDescripcion());
        if (palabraExistente != null) {
            return palabraExistente;
        }

        // Si no existe, ya viene con sus definiciones asociadas
        palabraRepositorio.guardar(palabra); // Cascade guarda las definiciones

        return palabra;
    }

    private Palabra obtenerAleatoria(List<Palabra> lista) {
        return lista.get(new Random().nextInt(lista.size()));
    }
}


