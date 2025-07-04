package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Ronda;

import java.util.List;

public interface RondaRepository {
    void guardar(Ronda ronda);

    List<Ronda> buscarPorPartidaConPalabra(Long partidaId);

    int obtenerCantidadDeRondasPorPartida(Long partidaId);

    Ronda obtenerUltimaRondaDePartida(Long partidaId);

    void actualizar(Ronda ronda);

    Ronda buscarPorId(Long rondaId);

}
