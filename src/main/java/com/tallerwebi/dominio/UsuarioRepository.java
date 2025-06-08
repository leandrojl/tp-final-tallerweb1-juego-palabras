package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario;

import com.tallerwebi.dominio.model.Usuario;

public interface UsuarioRepository {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
}

