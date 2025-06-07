package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class LoginServiceImpl implements LoginService {
    private List<Usuario> usuarios = Arrays.asList(
            new Usuario("pepe1235421", "pepe@gmail.com", "abc123245"),
            new Usuario("lucas", "lucas@gmail.com", "12151gdsf"),
            new Usuario("nicolas", "nicolas@gmail.com", "bv2b132v1")
    );
    @Override
    public Usuario login(String nombre, String password) {
        Usuario usuario = buscarUsuario(nombre);
        if(usuario == null) {
            throw new DatosLoginIncorrectosException();
        }
        if(!usuario.getPassword().equals(password)) {
            throw new DatosLoginIncorrectosException();
        }
        return usuario;
    }

    @Override
    public Usuario buscarUsuario(String nombre) {
        for (Usuario u : usuarios) {
            if (u.getUsuario().equals(nombre)) {
                return u;
            }
        }
        return null;
    }
}
