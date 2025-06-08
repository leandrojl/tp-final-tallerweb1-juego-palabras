package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

public interface RegistroService {
    Usuario registrar(String nombre, String password);

    Usuario buscarUsuario(String nombre);
}
