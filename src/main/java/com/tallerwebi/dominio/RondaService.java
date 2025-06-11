package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;

import java.util.HashMap;
import java.util.List;

public interface RondaService {
    Palabra extraerPalabra(HashMap<Palabra, List<Definicion>> palabraYDef, String idioma);

    void desactivarRonda(Ronda rondaActual);
    Ronda crearNuevaRonda(Partida2 partida, String idioma);

    Ronda buscarRondaActivaPorPartidaId(Long id);

    Ronda guardar(Ronda rondaActual);
}
