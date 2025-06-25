package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Ronda;

import java.util.List;
import java.util.Optional;

public interface RondaRepository {
    void guardar(Ronda ronda);


    List<Ronda> buscarPorPartidaConPalabra(Long partidaId);

    int obtenerCantidadDeRondasPorPartida(Long partidaId);

    Ronda obtenerUltimaRondaDePartida(Long partidaId);

    Ronda buscarPorId(Long rondaId);
}
