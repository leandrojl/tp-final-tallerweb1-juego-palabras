package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PasswordMenorAOchoCaracteresException;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteException;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.RegistroService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class RegistroServiceImpl implements RegistroService {

    private final UsuarioRepository repositorioUsuario;

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
        this.repositorioUsuario.guardar(new Usuario(nombre, password));
        return null;
    }

    @Override
    public Usuario buscarUsuario(String usuario) {
        return this.repositorioUsuario.buscar(usuario);
    }

    @Override
    public List<Usuario> obtenerUsuariosLogueados() {
        return this.repositorioUsuario.obtenerTodosLosUsuariosLogueados();
    }
}