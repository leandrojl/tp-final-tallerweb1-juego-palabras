package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Partida;

import java.util.List;

public interface LobbyRepository {
    void guardar(Partida partida);

    List<Partida> obtenerPartidasEnEspera();

    void eliminarTodasLasPartidas();

    List<Partida> obtenerPartidasEnCurso();

    List<Partida> obtenerPartidasFinalizadas();

    List<Partida> obtenerPartidasCanceladas();

    List<Partida> obtenerPartidasPorNombre(String nombre);
}
