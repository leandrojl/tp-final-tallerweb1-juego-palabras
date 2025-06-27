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

    List<Usuario> obtenerUsuariosDeUnaPartida(Long number);

    UsuarioPartida obtenerUsuarioPartida(Usuario usuario, Partida2 partida);

    void borrarUsuarioPartidaAsociadaAlUsuario(Long idPartida, Long idUsuario);
}
