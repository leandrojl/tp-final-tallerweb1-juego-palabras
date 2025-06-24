package com.tallerwebi.dominio;

import java.util.List;

public class RondaDto {
    private String palabra;
    private String definicionTexto;
    private int numeroDeRonda;
    private List<JugadorPuntajeDto> jugadores;

    // Getters y setters


    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getDefinicionTexto() {
        return definicionTexto;
    }

    public void setDefinicionTexto(String definicionTexto) {
        this.definicionTexto = definicionTexto;
    }

    public int getNumeroDeRonda() {
        return numeroDeRonda;
    }

    public void setNumeroDeRonda(int numeroDeRonda) {
        this.numeroDeRonda = numeroDeRonda;
    }

    public List<JugadorPuntajeDto> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<JugadorPuntajeDto> jugadores) {
        this.jugadores = jugadores;
    }
}