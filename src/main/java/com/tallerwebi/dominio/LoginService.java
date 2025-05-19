package com.tallerwebi.dominio;

public interface LoginService {
    Usuario login(String nombre, String password);

    Usuario buscarUsuario(String nombre);
}
