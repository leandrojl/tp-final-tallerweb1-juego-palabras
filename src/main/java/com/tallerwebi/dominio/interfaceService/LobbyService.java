package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Partida;

import java.util.List;

public interface LobbyService {

    List<Partida> obtenerPartidasEnEspera();

    void guardar(Partida ingles);

    List<Partida> buscarPartidasPorNombre(String nombre);
}
