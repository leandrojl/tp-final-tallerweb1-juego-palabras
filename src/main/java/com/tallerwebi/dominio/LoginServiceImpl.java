package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class LoginServiceImpl implements LoginService {
    UsuarioRepository repositorioUsuario;

    @Autowired
    public LoginServiceImpl(UsuarioRepository repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario login(String usuario, String password) {
        Usuario usuarioEncontrado = buscarUsuario(usuario);
        if(usuarioEncontrado == null) {
            throw new DatosLoginIncorrectosException();
        }
        if(!usuarioEncontrado.getPassword().equals(password)) {
            throw new DatosLoginIncorrectosException();
        }
        this.repositorioUsuario.guardar(usuarioEncontrado);
        return usuarioEncontrado;
    }

    @Override
    public Usuario buscarUsuario(String usuario) {
        return this.repositorioUsuario.buscar(usuario);
    }
}