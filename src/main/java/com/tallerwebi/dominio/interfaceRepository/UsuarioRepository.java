package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Usuario;

import java.io.Serializable;

public interface UsuarioRepository {

    Usuario buscarUsuario(String email, String password);
    Serializable guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);

    Usuario buscarPorId(long i);
}