package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario_Partida;

public interface UsuarioPartidaRepository {
    Usuario_Partida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);
}
