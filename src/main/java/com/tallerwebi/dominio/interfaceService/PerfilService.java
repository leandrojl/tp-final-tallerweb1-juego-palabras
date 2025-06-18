package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Usuario;

import java.util.Map;

public interface PerfilService {
    Map<String, Object> obtenerDatosDePerfil(Usuario usuario);

    Usuario buscarDatosDeUsuarioPorId(int i);

    double obtenerWinrate(Usuario usuario);
}
