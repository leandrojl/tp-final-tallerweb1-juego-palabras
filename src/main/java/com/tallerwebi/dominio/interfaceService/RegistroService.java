package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Usuario;

public interface RegistroService {
    Usuario registrar(String nombre, String password);

    Usuario buscarUsuario(String nombre);
}
