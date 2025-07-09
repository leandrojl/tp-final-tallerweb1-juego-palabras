package com.tallerwebi.dominio.interfaceService;


import com.tallerwebi.dominio.model.Ronda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RondaService {

    Ronda crearRonda(Long partidaId, String idioma);

    Ronda obtenerUltimaRondaDePartida(Long partidaId);

    // Map<String, List<String>> obtenerPalabraYDefinicion(String idioma);


}
