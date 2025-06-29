package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Partida2;

import java.util.List;

public interface LobbyService {

    List<Partida2> obtenerPartidasEnEspera();

    void guardar(Partida2 ingles);

    List<Partida2> buscarPartidasPorNombre(String nombre);
}
