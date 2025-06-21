package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Partida2;

import java.io.Serializable;

public interface PartidaRepository {
    Partida2 buscarPorId(Long id);

    // Ejemplo: guardar una partida (por si querés usarlo después)
    void guardar(Partida2 partida);

    Serializable crearPartida(Partida2 nuevaPartida);
}
