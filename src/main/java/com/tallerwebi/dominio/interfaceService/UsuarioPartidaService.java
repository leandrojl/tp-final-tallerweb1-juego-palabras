package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Usuario;

public interface UsuarioPartidaService {

    void agregarUsuarioAPartida(Long idUsuario, Long idPartida, int puntaje, boolean gano, Estado estado);


}
