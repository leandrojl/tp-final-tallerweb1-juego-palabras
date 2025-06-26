package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;

import java.util.List;

public interface UsuarioService {

Usuario buscarPorId(Long id);

    String obtenerNombrePorId(Long usuarioId);
}
