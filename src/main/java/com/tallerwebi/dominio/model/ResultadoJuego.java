package com.tallerwebi.dominio.model;

import java.util.List;
import java.util.Map;

public class ResultadoJuego {
    private String jugadorId;
    private boolean correcto;
    private int puntosObtenidos;
    private String palabra;
    private boolean nuevaRonda;
    private boolean partidaTerminada;
    private String nuevaPalabra;
    private String nuevaDefinicion;
    private Map<String, Integer> puntajes;
    private List<Map.Entry<String, Integer>> rankingFinal;

    // Getters y setters completos...
    public String getJugadorId() { return jugadorId; }
    public void setJugadorId(String jugadorId) { this.jugadorId = jugadorId; }

    public boolean isCorrecto() { return correcto; }
    public void setCorrecto(boolean correcto) { this.correcto = correcto; }

    public int getPuntosObtenidos() { return puntosObtenidos; }
    public void setPuntosObtenidos(int puntosObtenidos) { this.puntosObtenidos = puntosObtenidos; }

    public String getPalabra() { return palabra; }
    public void setPalabra(String palabra) { this.palabra = palabra; }

    public boolean isNuevaRonda() { return nuevaRonda; }
    public void setNuevaRonda(boolean nuevaRonda) { this.nuevaRonda = nuevaRonda; }

    public boolean isPartidaTerminada() { return partidaTerminada; }
    public void setPartidaTerminada(boolean partidaTerminada) { this.partidaTerminada = partidaTerminada; }

    public String getNuevaPalabra() { return nuevaPalabra; }
    public void setNuevaPalabra(String nuevaPalabra) { this.nuevaPalabra = nuevaPalabra; }

    public String getNuevaDefinicion() { return nuevaDefinicion; }
    public void setNuevaDefinicion(String nuevaDefinicion) { this.nuevaDefinicion = nuevaDefinicion; }

    public Map<String, Integer> getPuntajes() { return puntajes; }
    public void setPuntajes(Map<String, Integer> puntajes) { this.puntajes = puntajes; }

    public List<Map.Entry<String, Integer>> getRankingFinal() { return rankingFinal; }
    public void setRankingFinal(List<Map.Entry<String, Integer>> rankingFinal) { this.rankingFinal = rankingFinal; }
}