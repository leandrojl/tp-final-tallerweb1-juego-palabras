package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Ronda;

import java.util.List;

public interface RondaRepository {
    void guardar(Ronda ronda);


    List<Ronda> buscarPorPartidaConPalabra(Long partidaId);

    int obtenerCantidadDeRondasPorPartida(Long partidaId);
}
