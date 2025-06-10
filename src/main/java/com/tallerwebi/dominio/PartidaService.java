package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida2;

import java.util.List;


public interface PartidaService {
    Partida2 iniciarNuevaPartida(String jugadorId, String nombre);
    Partida2 obtenerPartida(String jugadorId);
    void eliminarPartida(String jugadorId);


}

