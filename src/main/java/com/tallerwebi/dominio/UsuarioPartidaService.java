package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaService {
    void actualizar(UsuarioPartida usuarioPartida);

    UsuarioPartida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);

    List<UsuarioPartida> buscarListaDeUsuariosPartidaPorPartidaId(Long id);

    UsuarioPartida buscarPorUsuarioId(Long usuarioId);
}
