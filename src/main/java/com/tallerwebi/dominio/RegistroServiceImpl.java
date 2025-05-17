package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class RegistroServiceImpl implements RegistroService {
    private List<Usuario> usuarios = Arrays.asList(
            new Usuario("pepe1235421", "pepe@gmail.com", "abc123245"),
            new Usuario("lucas", "lucas@gmail.com", "12151gdsf"),
            new Usuario("nicolas", "nicolas@gmail.com", "bv2b132v1")
    );
    @Override
    public Usuario registrar(String nombre, String password) {
        Usuario usuario = buscarUsuario(nombre);
        if(usuario != null) {
            return null;
        }
        if(password.length() < 8){
            return null;
        }
        return new Usuario();
    }

    @Override
    public Usuario buscarUsuario(String nombre) {
            for (Usuario u : usuarios) {
                if (u.getNombre().equals(nombre)) {
                    return u;
                }
            }
            return null;
    }
}
