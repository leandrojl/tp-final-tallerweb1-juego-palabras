package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Usuario;

import java.io.Serializable;
import java.util.List;

public interface UsuarioRepository {

    Usuario buscarUsuario(String email, String password);

    Serializable guardar(Usuario usuario);

    Usuario buscar(String email);

    void modificar(Usuario usuario);

    Usuario buscarPorId(Long i);

    List<Usuario> obtenerTodosLosUsuariosLogueados();

    String obtenerNombrePorId(Long usuarioId);

    Usuario obtenerUsuarioPorNombre(String name);

    void actualizarEstado(Long idUsuario, boolean estado);
}