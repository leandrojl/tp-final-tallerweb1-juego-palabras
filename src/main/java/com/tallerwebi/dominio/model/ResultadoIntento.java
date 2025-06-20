package com.tallerwebi.dominio.model;

public class ResultadoIntento  {
    private boolean correcto;
    private String jugadorId;
    private String palabra;
    private int puntosObtenidos;
    private boolean nuevaRonda;
    private boolean partidaTerminada;
    private String nuevaPalabra;
    private String nuevaDefinicion;

    // Getters y setters
    public boolean isCorrecto() { return correcto; }
    public void setCorrecto(boolean correcto) { this.correcto = correcto; }

    public String getJugadorId() { return jugadorId; }
    public void setJugadorId(String jugadorId) { this.jugadorId = jugadorId; }

    public String getPalabra() { return palabra; }
    public void setPalabra(String palabra) { this.palabra = palabra; }

    public int getPuntosObtenidos() { return puntosObtenidos; }
    public void setPuntosObtenidos(int puntosObtenidos) { this.puntosObtenidos = puntosObtenidos; }

    public boolean isNuevaRonda() { return nuevaRonda; }
    public void setNuevaRonda(boolean nuevaRonda) { this.nuevaRonda = nuevaRonda; }

    public boolean isPartidaTerminada() { return partidaTerminada; }
    public void setPartidaTerminada(boolean partidaTerminada) { this.partidaTerminada = partidaTerminada; }

    public String getNuevaPalabra() { return nuevaPalabra; }
    public void setNuevaPalabra(String nuevaPalabra) { this.nuevaPalabra = nuevaPalabra; }

    public String getNuevaDefinicion() { return nuevaDefinicion; }
    public void setNuevaDefinicion(String nuevaDefinicion) { this.nuevaDefinicion = nuevaDefinicion; }
}
