package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PasswordMenorAOchoCaracteresException;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteException;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.infraestructura.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Transactional
@Service
public class RegistroServiceImpl implements RegistroService {

    private final UsuarioRepository repositorioUsuario;
    /*private List<Usuario> usuarios = Arrays.asList(
            new Usuario("pepe1235421", "pepe@gmail.com", "abc123245"),
            new Usuario("lucas", "lucas@gmail.com", "12151gdsf"),
            new Usuario("nicolas", "nicolas@gmail.com", "bv2b132v1")
    );*/
    @Autowired
    public RegistroServiceImpl(UsuarioRepository repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario registrar(String nombre, String password) {
        Usuario usuario = buscarUsuario(nombre);
        if(usuario != null) {
            throw new UsuarioExistenteException();
        }
        if(password.length() < 8){
            throw new PasswordMenorAOchoCaracteresException();
        }
        //Aca agregaria al usuario a la base de datos
        return new Usuario();
    }

    @Override
    public Usuario buscarUsuario(String usuario) {
        return this.repositorioUsuario.buscar(usuario);
    }
}