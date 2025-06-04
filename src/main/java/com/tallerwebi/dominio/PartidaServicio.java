package com.tallerwebi.dominio;

import com.tallerwebi.infraestructura.Partida;

import java.util.*;


public interface PartidaServicio {
    Partida iniciarNuevaPartida(String jugadorId, String nombre);
    Partida obtenerPartida(String jugadorId);
    void eliminarPartida(String jugadorId);
}

