package com.tallerwebi.dominio.ServicioImplementacion;


import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.buscarPorId(id);
    }

    @Override
    @Transactional
    public String obtenerNombrePorId(Long usuarioId) {
        return usuarioRepository.obtenerNombrePorId(usuarioId);
    }

    @Override
    public Usuario obtenerUsuarioPorId(Long usuarioId) {
        return usuarioRepository.buscarPorId(usuarioId);
    }

    @Override
    public Usuario obtenerUsuarioPorNombre(String name) {
        return usuarioRepository.obtenerUsuarioPorNombre(name);
    }
}
