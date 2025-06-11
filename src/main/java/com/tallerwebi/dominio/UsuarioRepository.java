package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

import java.io.Serializable;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario buscarUsuario(String email, String password);
    Serializable guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);

    Usuario buscarPorId(long i);
}