package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;

import java.util.List;


public interface PartidaService {
    public Partida2 iniciarNuevaPartida(String nombrePartida);

    boolean estaTerminada(Partida2 partida);
//    Partida2 obtenerPartida(String jugadorId);
//    void eliminarPartida(String jugadorId);


}

