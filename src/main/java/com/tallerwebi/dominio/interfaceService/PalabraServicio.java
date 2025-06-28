package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Palabra;

public interface PalabraServicio {
    Palabra obtenerPalabraAleatoriaPorIdioma(String idioma);
    Palabra obtenerPalabraAleatoriaMixta(); // castellano o inglés al azar


    Palabra obtenerPalabraConDefinicionesDesdeHelper(String idioma);


}
