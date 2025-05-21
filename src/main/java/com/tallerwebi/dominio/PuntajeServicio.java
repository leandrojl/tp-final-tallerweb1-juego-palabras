package com.tallerwebi.dominio;

import java.util.Map;

public interface PuntajeServicio {
    void registrarPuntos(String jugadorId, int puntos);
    int obtenerPuntaje(String jugadorId);
    Map<Jugador, Integer> obtenerTodosLosPuntajes();
    void registrarJugador(String jugadorId, Jugador pass);
}
