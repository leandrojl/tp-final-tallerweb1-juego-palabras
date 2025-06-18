package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Usuario;

public interface LoginService {
    Usuario login(String nombre, String password);

    Usuario buscarUsuario(String nombre);
}
