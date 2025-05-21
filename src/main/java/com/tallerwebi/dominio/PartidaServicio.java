package com.tallerwebi.dominio;

import java.util.*;


public interface PartidaServicio {
    Partida iniciarNuevaPartida(String jugadorId, String nombre);
    Partida obtenerPartida(String jugadorId);
    void eliminarPartida(String jugadorId);
}

