package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Palabra;

import java.util.List;
import java.util.Map;

public interface PalabraServicio {
    Palabra obtenerPalabraAleatoriaPorIdioma(String idioma);
    Palabra obtenerPalabraAleatoriaMixta(); // castellano o inglés al azar


    Palabra obtenerPalabraConDefinicionesDesdeHelper(String idioma);


}
