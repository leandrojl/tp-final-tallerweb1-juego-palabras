package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;

public interface RondaService {
    Ronda crearNuevaRonda(Partida2 partida, String idioma);
}
