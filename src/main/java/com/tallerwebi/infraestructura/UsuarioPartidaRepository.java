package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario_Partida;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaRepository {
    void guardar(UsuarioPartida usuarioPartida);

    void borrar(UsuarioPartida usuarioPartida);

    UsuarioPartida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);
    List<UsuarioPartida> buscarListaDeUsuariosPartidaPorPartidaId(Long id);

    UsuarioPartida buscarPorUsuarioId(Long jugadorId);

    void actualizar(UsuarioPartida usuarioPartida);
}
