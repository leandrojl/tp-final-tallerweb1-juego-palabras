package com.tallerwebi.dominio.interfaceRepository;


import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaRepository {
    int getCantidadDePartidasDeJugador(Usuario usuario);

    int getCantidadDePartidasGanadasDeJugador(Usuario usuario);

    double getWinrate(Usuario usuario);

    List<Object[]> obtenerRankingGlobal();

    int contarUsuariosEnPartida(Long idPartida);

    List<UsuarioPartida> buscarPorPartida(Long idPartida);

    List<Usuario> obtenerUsuariosDeUnaPartida(Long number);

    UsuarioPartida obtenerUsuarioPartida(Usuario usuario, Partida partida);

    void borrarUsuarioPartidaAsociadaAlUsuario(Long idPartida, Long idUsuario);

    Partida obtenerPartida(Long idPartida);

    void agregarUsuarioAPartida(Long idUsuario, Long idPartida,
                                int puntaje, boolean gano, Estado estado);

    String obtenerNombreDeUsuarioEnLaPartida(Long usuarioId, Long idPartida);

    void guardarUsuarioPartida(UsuarioPartida usuarioPartida);

    void actualizarPuntaje(Long usuarioId, Long partidaId, int nuevoPuntaje);

    int obtenerPuntaje(Long usuarioId, Long partidaId);

    List<UsuarioPartida> obtenerUsuarioPartidaPorPartida(Long partidaId);

    void sumarPuntaje(Long usuarioId, Long partidaId, int puntos);

    Usuario obtenerUsuarioPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);

    UsuarioPartida obtenerUsuarioEspecificoPorPartida(Long usuarioId, Long partidaId);

    void actualizar(UsuarioPartida relacion);


    UsuarioPartida obtenerUsuarioPartida(Long idUsuario, Long idPartida);


    void cancelarPartidaDeUsuario(Long idUsuario, Long idPartida);

    void actualizarEstado(Long idPartida, Estado estado);


    List<Usuario> obtenerUsuariosListosDeUnaPartida(Long idPartida);

    Usuario obtenerUsuarioDeUnaPartidaPorSuNombreUsuario(String nombreUsuarioDelPrincipal, Long idPartida);

    void cambiarEstado(Long idUsuarioAExpulsar, Long idPartida, Estado estado);

    int cantidadDeJugadoresActivosEnPartida(Long partidaId);

    void finalizarPartidaParaTodos(Long partidaId, Estado estado);


    void marcarTodasLasPartidasComoFinalizadas(Long idUsuario, Estado estado);

    List<Usuario> obtenerTodosLosUsuariosConPartidas();

    void actualizarGanador(Long id);

    Usuario buscarPorNombre(String nombreUsuario);

}
