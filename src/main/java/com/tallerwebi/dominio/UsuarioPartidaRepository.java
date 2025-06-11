package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

public interface UsuarioPartidaRepository {
    int getCantidadDePartidasDeJugador(Usuario usuario);
}
