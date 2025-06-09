package com.tallerwebi.dominio;


import com.tallerwebi.dominio.model.Definicion;

import java.util.HashMap;
import java.util.List;

public interface PalabraService {
    HashMap<String, List<Definicion>> traerPalabraYDefinicion(String idioma);
}
