package com.tallerwebi.dominio.model;

public class EstadoJugador {
    private String username;
    private boolean estaListo;

    public EstadoJugador(String username, boolean estaListo) {
        this.username = username;
        this.estaListo = estaListo;
    }

    public EstadoJugador() {

    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEstaListo() {
        return estaListo;
    }

    public void setEstaListo(boolean estaListo) {
        this.estaListo = estaListo;
    }
}
