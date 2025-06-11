package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

public interface UsuarioService {
    Usuario buscarPorId(Long usuarioId);
}
