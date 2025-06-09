package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Partida2;
import org.springframework.stereotype.Repository;

@Repository
public class PartidaRepositoryImpl implements PartidaRepository {
    @Override
    public Partida2 buscarPorId(Long partidaId) {
        return null;
    }

    @Override
    public void actualizar(Partida2 partida) {

    }
}
