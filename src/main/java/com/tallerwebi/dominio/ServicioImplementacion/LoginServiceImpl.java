package com.tallerwebi.dominio.ServicioImplementacion;

import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.LoginService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class LoginServiceImpl implements LoginService {
    UsuarioRepository repositorioUsuario;

    @Autowired
    public LoginServiceImpl(UsuarioRepository repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario login(String nombreUsuario, String password) {
        Usuario usuarioEncontrado = buscarUsuario(nombreUsuario);
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
    public Usuario buscarUsuario(String nombreUsuario) {
        return this.repositorioUsuario.buscar(nombreUsuario);
    }
}