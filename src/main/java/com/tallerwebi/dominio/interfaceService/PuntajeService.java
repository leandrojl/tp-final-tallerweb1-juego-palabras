package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Jugador;

import java.util.Map;

public interface PuntajeService {
    void registrarPuntos(String jugadorId, int puntos);
    int obtenerPuntaje(String jugadorId);
    Map<Jugador, Integer> obtenerTodosLosPuntajes();
    void registrarJugador(String jugadorId, Jugador pass);
}
