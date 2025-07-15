package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Usuario;
import org.json.JSONObject;

public interface UsuarioService {

    Usuario buscarPorId(Long id);

    String obtenerNombrePorId(Long usuarioId);

    Usuario obtenerUsuarioPorId(Long usuarioId);

    Usuario obtenerUsuarioPorNombre(String name);

    String obtenerEmailPorId(long idUsuario);

    void agregarMonedasAlUsuario(JSONObject respuesta);



    int getMonedasPorIdUsuario(Long idUsuario);
}
