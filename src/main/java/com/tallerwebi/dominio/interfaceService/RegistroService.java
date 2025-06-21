package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Usuario;

import java.util.List;

public interface RegistroService {
    Usuario registrar(String nombre, String password);

    Usuario buscarUsuario(String nombre);

    List<Usuario> obtenerUsuariosLogueados();
}
