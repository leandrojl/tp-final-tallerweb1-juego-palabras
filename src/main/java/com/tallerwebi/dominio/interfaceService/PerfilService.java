package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Usuario;

import java.util.Map;

public interface PerfilService {
    Map<String, Object> obtenerDatosDePerfil(Usuario usuario);

    Usuario buscarDatosDeUsuarioPorId(Long id);

    double obtenerWinrate(Usuario usuario);

    Usuario obtenerDatosDelPerfilPorId(Long usuarioId);
}
