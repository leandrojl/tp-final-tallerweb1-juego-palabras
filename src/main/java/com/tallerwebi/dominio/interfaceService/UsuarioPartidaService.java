package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import java.util.List;

public interface UsuarioPartidaService {

    void agregarUsuarioAPartida(Long idUsuario, Long idPartida, int puntaje, boolean gano, Estado estado);

    String obtenerNombreDeUsuarioEnLaPartida(Long usuarioId, Long idPartida);

    void asociarUsuarioConPartida(Usuario usuario, Partida partida);
    void actualizarPuntaje(Long usuarioId, Long partidaId, int nuevoPuntaje);
    int obtenerPuntaje(Long usuarioId, Long partidaId);

    List<UsuarioPartida> obtenerPorPartida(Long partidaId);

    void sumarPuntos(Long usuarioId, Long partidaId, int puntos);

    Usuario obtenerUsuarioPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);

    void marcarComoPerdedor(Long usuarioId, Long partidaId);

    UsuarioPartida obtenerUsuarioEspecificoPorPartida(Long usuarioId, Long partidaId);

    UsuarioPartida buscarUsuarioPartida(Long idPartida, Long usuarioId);


    void cancelarPartidaDeUsuario(Long idUsuario, Long idPartida);

    void cambiarEstado(Long idUsuarioAExpulsar, Long idPartida, Estado estado);
}
