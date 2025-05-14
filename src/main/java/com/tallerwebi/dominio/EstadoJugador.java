package com.tallerwebi.dominio;

public class EstadoJugador {
    private String jugadorId;
    private boolean estaListo;

    public EstadoJugador(String nombre, boolean estaListo) {
        this.jugadorId = nombre;
        this.estaListo = estaListo;
    }

    public String getJugadorId() {
        return jugadorId;
    }

    public void setJugadorId(String jugadorId) {
        this.jugadorId = jugadorId;
    }

    public boolean isEstaListo() {
        return estaListo;
    }

    public void setEstaListo(boolean estaListo) {
        this.estaListo = estaListo;
    }
}
