package com.tallerwebi.dominio;

import java.util.*;

public class Partida {
    private Map<String, Integer> puntajes = new HashMap<>();
    private Map<String, String> nombres = new HashMap<>();

    private int rondaActual;
    private boolean partidaTerminada;
    private String palabraActual;
    private String definicionActual;

    private static final int MAX_RONDAS = 5;

    public Partida() {
        this.puntajes = new HashMap<>();
        this.nombres = new HashMap<>();
        this.rondaActual = 0;
        this.partidaTerminada = false;
    }


    public void agregarJugador(String jugadorId, String nombre) {
        puntajes.putIfAbsent(jugadorId, 0);
        nombres.putIfAbsent(jugadorId, nombre);
    }

    /**
     * Avanza la ronda y actualiza palabra y definición.
     * @return true si hay siguiente ronda, false si terminó.
     */
    public boolean avanzarRonda(String nuevaPalabra, String nuevaDefinicion) {
        if (rondaActual < MAX_RONDAS) {
            rondaActual++;
            this.palabraActual = nuevaPalabra;
            this.definicionActual = nuevaDefinicion;
            return true;
        } else {
            partidaTerminada = true;
            return false;
        }
    }

    // Getters y setters
    public int getRondaActual() { return rondaActual; }
    public boolean isPartidaTerminada() { return partidaTerminada; }
    public String getPalabraActual() { return palabraActual; }
    public String getDefinicionActual() { return definicionActual; }
    public Set<String> getJugadorIds() {
        return nombres.keySet();
    }

    public String getNombre(String jugadorId) {
        return nombres.getOrDefault(jugadorId, "Jugador_" + jugadorId);
    }

}


