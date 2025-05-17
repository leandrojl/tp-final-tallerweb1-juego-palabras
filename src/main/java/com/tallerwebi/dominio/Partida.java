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
        if (!jugadores.containsKey(jugadorId)) {
            jugadores.put(jugadorId, 0); // Inicializamos con 0 puntos
        }
    }

    public void actualizarPuntos(String jugadorId, int puntos) {
        if (jugadores.containsKey(jugadorId)) {
            jugadores.put(jugadorId, jugadores.get(jugadorId) + puntos);
        }
    }

    public void avanzarRonda() {
        if (rondaActual < 5) {
            rondaActual++;
        } else {
            partidaTerminada = true;
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
        this.palabraActual = "example";
    }

    public String getDefinicionActual() {
        return definicionActual;
    }

    public void setDefinicionActual(String definicionActual) {
        this.definicionActual = definicionActual;
    }

    public Object getPuntaje(String jugadorId) { return jugadores.get(jugadorId); }
}
