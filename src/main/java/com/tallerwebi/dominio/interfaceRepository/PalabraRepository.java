package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Palabra;

import java.util.List;

public interface PalabraRepository {
    List<Palabra> buscarPorIdioma(String idioma);
    List<Palabra> buscarTodas();

    Palabra buscarPorTexto(String descripcion);

    void guardar(Palabra palabra);


}
