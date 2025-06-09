package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;

import java.util.List;

public interface RondaRepository {
    Ronda guardar(Ronda ronda);

    Ronda buscarPorId(Long id);

    List<Ronda> buscarPorPartida(Partida2 partida);

    List<Ronda> buscarPorEstado(Estado estado);

    List<Ronda> buscarPorPartidaYEstado(Partida2 partida, Estado estado);

    Ronda buscarUltimaRondaDePartida(Partida2 partida);

    List<Ronda> buscarTodasLasRondas();

    Long contarRondasDePartida(Partida2 partida);

    void eliminar(Ronda ronda);

    void actualizar(Ronda ronda);

    Ronda buscarRondaActivaPorPartidaId(Long id);
}
