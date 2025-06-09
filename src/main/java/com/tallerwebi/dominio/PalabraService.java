package com.tallerwebi.dominio;


import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;

import java.util.HashMap;
import java.util.List;

public interface PalabraService {
    HashMap<Palabra, List<Definicion>> traerPalabraYDefinicion(String idioma);
}
