package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public class UsuarioPartidaRepositoryImpl implements UsuarioPartidaRepository{

    @Override
    public UsuarioPartida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId) {
        return null;
    }

    @Override
    public List<UsuarioPartida> buscarPorPartidaId(Long id) {
        return List.of();
    }
}
