package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Usuario;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public class UsuarioPartidaRepositoryImpl implements UsuarioRepository{
    @Override
    public Usuario buscarUsuario(String email, String password) {
        return null;
    }

    @Override
    public Serializable guardar(Usuario usuario) {
        return null;
    }

    @Override
    public Usuario buscar(String email) {
        return null;
    }

    @Override
    public void modificar(Usuario usuario) {

    }

    @Override
    public Usuario buscarPorId(Long id) {
        return null;
    }
}
