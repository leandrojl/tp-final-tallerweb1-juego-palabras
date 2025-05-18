package com.tallerwebi.dominio;

public class SalaDeEspera {
    private Usuario jugador1;
    private Usuario jugador2;
    private boolean jugador1Listo;
    private boolean jugador2Listo;

    public SalaDeEspera() {
        this.jugador1 = null;
        this.jugador2 = null;
        this.jugador1Listo = false;
        this.jugador2Listo = false;
    }
    // Getters y setters
    public Usuario getJugador1() {
        return jugador1;
    }

    public void setJugador1(Usuario jugador1) {
        this.jugador1 = jugador1;
    }

    public Usuario getJugador2() {
        return jugador2;
    }

    public void setJugador2(Usuario jugador2) {
        this.jugador2 = jugador2;
    }

    public boolean getJugador1Listo() {
        return jugador1Listo;
    }

    public boolean getJugador2Listo() {
        return jugador2Listo;
    }

    public void setJugador1Listo(boolean jugador1Listo) {
        if (this.jugador1 != null) {
            this.jugador1Listo = jugador1Listo;
        }
    }


    public void setJugador2Listo(boolean jugador2Listo) {
        if (this.jugador2 != null) {
            this.jugador2Listo = jugador2Listo;
        }
    }

    // Método para verificar si ambos jugadores están listos
    public boolean estanAmbosListos() {
        return jugador1Listo && jugador2Listo;
    }

    public void agregarJugador(Jugador jugador1) {
    }
}