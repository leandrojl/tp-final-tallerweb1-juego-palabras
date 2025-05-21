package com.tallerwebi.dominio;

public interface RegistroService {
    Usuario registrar(String nombre, String password);

    Usuario buscarUsuario(String nombre);
}
