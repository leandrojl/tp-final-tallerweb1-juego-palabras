package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Partida2;

public interface PartidaRepository {
    Partida2 buscarPorId(Long partidaId);

    void actualizar(Partida2 partida);
}
