package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaRepository {
    int getCantidadDePartidasDeJugador(Usuario usuario);

    int getCantidadDePartidasGanadasDeJugador(Usuario usuario);

    double getWinrate(Usuario usuario);

    List<Object[]> obtenerRanking();


    void guardarUsuarioPartida(UsuarioPartida usuarioPartida);

    void actualizarPuntaje(Long usuarioId, Long partidaId, int nuevoPuntaje);

    int obtenerPuntaje(Long usuarioId, Long partidaId);

    List<UsuarioPartida> obtenerUsuarioPartidaPorPartida(Long partidaId);

    void sumarPuntaje(Long usuarioId, Long partidaId, int puntos);

    Usuario obtenerUsuarioPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId);

    UsuarioPartida obtenerUsuarioEspecificoPorPartida(Long usuarioId, Long partidaId);

    void actualizar(UsuarioPartida relacion);

}
