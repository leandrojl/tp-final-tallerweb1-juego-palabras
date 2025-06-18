package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Partida2;

import java.util.List;

public interface LobbyRepository {
    void guardar(Partida2 partida);

    List<Partida2> obtenerPartidasEnEspera();

    void eliminarTodasLasPartidas();

    List<Partida2> obtenerPartidasEnCurso();

    List<Partida2> obtenerPartidasFinalizadas();

    List<Partida2> obtenerPartidasCanceladas();
}
