package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida2;

import java.util.List;

public interface LobbyRepository {
    void guardar(Partida2 partida);

    List<Partida2> obtenerPartidasEnEspera();
}
