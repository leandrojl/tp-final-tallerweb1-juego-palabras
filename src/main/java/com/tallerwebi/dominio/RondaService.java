package com.tallerwebi.dominio;


import com.tallerwebi.dominio.model.Ronda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RondaService {

    Ronda crearRonda(Long partidaId, String idioma);

   // Map<String, List<String>> obtenerPalabraYDefinicion(String idioma);


}
