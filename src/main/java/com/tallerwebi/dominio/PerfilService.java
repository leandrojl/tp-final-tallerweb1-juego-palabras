package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

import java.util.Map;

public interface PerfilService {
    Map<String, Object> obtenerDatosDePerfil();

    Usuario buscarDatosDeUsuarioPorId(int i);

    double obtenerWinrate(int i);
}
