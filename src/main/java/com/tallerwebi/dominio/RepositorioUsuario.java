package com.tallerwebi.dominio;

import java.io.Serializable;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    Serializable guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
}

