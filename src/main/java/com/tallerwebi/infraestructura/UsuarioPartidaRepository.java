package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario_Partida;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaRepository {
    void guardar(Partida2 partida);

    void borrar(Partida2 partida);

    UsuarioPartida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);
    List<UsuarioPartida> buscarPorPartidaId(Long id);


}
