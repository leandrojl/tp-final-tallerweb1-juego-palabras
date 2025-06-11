package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.infraestructura.UsuarioRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario buscarPorId(Long usuarioId) {
        return usuarioRepository.buscarPorId(usuarioId);
    }
}
