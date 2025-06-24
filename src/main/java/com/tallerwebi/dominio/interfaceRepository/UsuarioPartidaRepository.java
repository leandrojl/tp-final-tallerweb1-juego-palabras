package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;

import java.util.List;

public interface UsuarioPartidaRepository {
    int getCantidadDePartidasDeJugador(Usuario usuario);

    int getCantidadDePartidasGanadasDeJugador(Usuario usuario);

    double getWinrate(Usuario usuario);

    List<Object[]> obtenerRanking();

    int contarUsuariosEnPartida(Long idPartida);

    List<UsuarioPartida> buscarPorPartida(Long idPartida);
}
