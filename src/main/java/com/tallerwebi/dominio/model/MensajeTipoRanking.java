package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.DTO.JugadorPuntajeDto;

import java.util.List;

public class MensajeTipoRanking {

    private String tipo;
    private List<JugadorPuntajeDto> jugadores;

    public MensajeTipoRanking() {}


    public MensajeTipoRanking(String tipo, List<JugadorPuntajeDto> jugadores) {
        this.tipo = tipo;
        this.jugadores = jugadores;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public List<JugadorPuntajeDto> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<JugadorPuntajeDto> jugadores) {
        this.jugadores = jugadores;
    }
}
