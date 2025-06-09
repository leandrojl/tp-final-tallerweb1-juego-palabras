package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario_Partida;
import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaRepository {
    UsuarioPartida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);

    List<UsuarioPartida> buscarPorPartidaId(Long id);
}
