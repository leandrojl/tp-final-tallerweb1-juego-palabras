package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;

public interface PartidaRepository {

    void guardar(Partida2 partida);

    void borrar(Partida2 partida);

    Partida2 buscarPorId(Long partidaId);
    void actualizarEstado(Long partidaId, Estado nuevoEstado);

}
