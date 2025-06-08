package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;

import java.util.List;

public interface LobbyService {

    List<Partida2> obtenerPartidasEnEspera();

    void guardar(Partida2 ingles);
}
