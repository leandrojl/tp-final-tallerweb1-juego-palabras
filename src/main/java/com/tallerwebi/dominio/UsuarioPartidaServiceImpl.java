package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.UsuarioPartida;
import com.tallerwebi.infraestructura.UsuarioPartidaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioPartidaServiceImpl implements UsuarioPartidaService{

    private final UsuarioPartidaRepository usuarioPartidaRepository;

    public UsuarioPartidaServiceImpl(UsuarioPartidaRepository usuarioPartidaRepository){
        this.usuarioPartidaRepository = usuarioPartidaRepository;
    }

    @Override
    public void actualizar(UsuarioPartida usuarioPartida) {
        usuarioPartidaRepository.actualizar(usuarioPartida);
    }

    @Override
    public UsuarioPartida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId) {
        return this.usuarioPartidaRepository.buscarPorUsuarioIdYPartidaId(usuarioId, partidaId);
    }

    @Override
    public List<UsuarioPartida> buscarListaDeUsuariosPartidaPorPartidaId(Long id) {
        return this.usuarioPartidaRepository.buscarListaDeUsuariosPartidaPorPartidaId(id);
    }

    @Override
    public UsuarioPartida buscarPorUsuarioId(Long usuarioId) {
        return this.usuarioPartidaRepository.buscarPorUsuarioId(usuarioId);
    }
}
