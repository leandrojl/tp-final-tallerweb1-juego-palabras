package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaService {
    void asociarUsuarioConPartida(Usuario usuario, Partida2 partida);
    void actualizarPuntaje(Long usuarioId, Long partidaId, int nuevoPuntaje);
    int obtenerPuntaje(Long usuarioId, Long partidaId);

    void marcarComoPerdedor(Long usuarioId, Long partidaId);

    UsuarioPartida obtenerUsuarioEspecificoPorPartida(Long usuarioId, Long partidaId);
}
