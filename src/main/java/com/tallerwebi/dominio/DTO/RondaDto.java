package com.tallerwebi.dominio.DTO;

import java.util.List;

public class RondaDto {
    private String tipo;
    private String palabra;
    private String definicionTexto;
    private int numeroDeRonda;
    private List<JugadorPuntajeDto> jugadores;
    private int rondasTotales;

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


    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getRondasTotales() {
        return rondasTotales;
    }
    public void setRondasTotales(int rondasTotales) {
        this.rondasTotales = rondasTotales;
    }
}