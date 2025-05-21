package com.tallerwebi.dominio;

public class Puntaje {
    private int puntajeTotal;

    public Puntaje() {
        this.puntajeTotal = 0;
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }

    public void sumarPuntos(int puntos) {
        this.puntajeTotal += puntos;
    }
}