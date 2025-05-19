package com.tallerwebi.dominio;

import java.util.HashMap;
import java.util.Map;

public class Partida {
    private Map<String, Integer> jugadores; // jugadorId -> puntos
    private int rondaActual;
    private boolean partidaTerminada;
    private String palabraActual;
    private String definicionActual;

    public Partida() {
        this.jugadores = new HashMap<>();
        this.rondaActual = 1;
        this.partidaTerminada = false;
    }

    // Métodos para gestionar la partida
    public void agregarJugador(String jugadorId) {
        jugadores.putIfAbsent(jugadorId, 0);
    }

    public void actualizarPuntos(String jugadorId, int puntos) {
        jugadores.put(jugadorId, jugadores.getOrDefault(jugadorId, 0) + puntos);
    }

    /**
     * Avanza la ronda. Si la ronda es menor a 5, incrementa y devuelve true.
     * Si la ronda ya era 5 o más, marca la partida terminada y devuelve false.
     */
    public boolean avanzarRonda() {
        if (rondaActual < 5) {
            rondaActual++;
            return true; // queda ronda para jugar
        } else {
            partidaTerminada = true;
            return false; // partida finalizada
        }
    }

    // Getters y setters
    public Map<String, Integer> getJugadores() {
        return jugadores;
    }

    public int getRondaActual() {
        return rondaActual;
    }

    public boolean isPartidaTerminada() {
        return partidaTerminada;
    }

    public String getPalabraActual() {
        return palabraActual;
    }

    public void setPalabraActual(String palabraActual) {
        this.palabraActual = palabraActual; // asigna el valor recibido, no "example"
    }

    public String getDefinicionActual() {
        return definicionActual;
    }

    public void setDefinicionActual(String definicionActual) {
        this.definicionActual = definicionActual;
    }

    public Integer getPuntaje(String jugadorId) {
        return jugadores.get(jugadorId);
    }
}

