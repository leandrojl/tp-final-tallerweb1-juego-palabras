package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Palabra;

import java.util.List;

public interface PalabraRepository {
    void guardar(Palabra palabra);
    List<Palabra> buscarPorIdioma(String idioma);
    List<Palabra> buscarTodas();
    Long contar();


}
