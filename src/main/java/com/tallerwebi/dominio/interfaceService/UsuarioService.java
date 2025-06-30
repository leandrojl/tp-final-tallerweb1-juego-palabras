package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Usuario;

public interface UsuarioService {

    Usuario buscarPorId(Long id);

    String obtenerNombrePorId(Long usuarioId);

    Usuario obtenerUsuarioPorId(Long usuarioId);

}
