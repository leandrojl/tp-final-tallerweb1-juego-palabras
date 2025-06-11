package com.tallerwebi.dominio;

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
private final UsuarioRepositoryImpl usuarioRepository;

    @Autowired
    public PerfilServiceImpl(UsuarioRepositoryImpl usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Map<String, Object> obtenerDatosDePerfil() {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("nombre", "Juan Perez");
        modelo.put("usuario", "Juancito123");
        modelo.put("edad", "15");
        modelo.put("winrate", "70%");
        modelo.put("fotoPerfil","fotoperfil1.png");
        return modelo;
    }

    @Override
    public Usuario buscarDatosDeUsuarioPorId(int i) {
        return usuarioRepository.buscarPorId(i);

    }

    @Override
    public double obtenerWinrate(int i) {
        return 0.0;
    }
}
