package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.PerfilService;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.infraestructura.UsuarioRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PerfilServiceImpl implements PerfilService {

private final UsuarioRepository usuarioRepository;
private final UsuarioPartidaRepository usuarioPartidaRepository;

@Autowired
    public PerfilServiceImpl(UsuarioRepository usuarioRepository, UsuarioPartidaRepository usuarioPartidaRepository) {
        this.usuarioRepository = usuarioRepository;
    this.usuarioPartidaRepository = usuarioPartidaRepository;
}

    @Override
    public Map<String, Object> obtenerDatosDePerfil(Usuario usuario) {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("nombre", "Juan");
        modelo.put("usuario", usuario.getNombreUsuario());
        modelo.put("Email", usuario.getEmail());
        modelo.put("winrate", usuarioPartidaRepository.getWinrate(usuario));
        modelo.put("fotoPerfil","fotoperfil1.png");
        return modelo;
    }

    @Override
    public Usuario buscarDatosDeUsuarioPorId(Long id) {
        return usuarioRepository.buscarPorId(id);

    }

    @Override
    public double obtenerWinrate(Usuario usuario) {
        return usuarioPartidaRepository.getWinrate(usuario);
    }

    @Override
    public Usuario obtenerDatosDelPerfilPorId(Long usuarioId) {
        return usuarioRepository.buscarPorId(usuarioId);
    }
}
