package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida;

import java.io.Serializable;

public interface PartidaRepository {
    Partida buscarPorId(Long id);

    // Ejemplo: guardar una partida (por si querés usarlo después)
    void guardar(Partida partida);

    Serializable crearPartida(Partida nuevaPartida);

    void actualizarEstado(Long idPartida,Estado estado);

    void cancelarPartidaDeUsuario(Long idUsuario, Long idPartida);

    boolean verificarSiEsElCreadorDePartida(Long idUsuario, Long idPartida);

    Estado verificarEstadoDeLaPartida(Long idPartida);

    Partida buscarPartidaPorId(Long idPartida);

    void modificarEstadoPartida(Partida partida, Estado estado);
}
