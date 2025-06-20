package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;

import java.util.List;

public class EstadoPartida {
    private Long partidaId;
    private Estado estado;
    private int rondaActual;
    private int rondasTotales;
    private String definicionActual;
    private String palabraActual;
    private List<String> jugadores;
    private boolean partidaTerminada;

    // Getters y setters
    public Long getPartidaId() { return partidaId; }
    public void setPartidaId(Long partidaId) { this.partidaId = partidaId; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public int getRondaActual() { return rondaActual; }
    public void setRondaActual(int rondaActual) { this.rondaActual = rondaActual; }

    public int getRondasTotales() { return rondasTotales; }
    public void setRondasTotales(int rondasTotales) { this.rondasTotales = rondasTotales; }

    public String getDefinicionActual() { return definicionActual; }
    public void setDefinicionActual(String definicionActual) { this.definicionActual = definicionActual; }

    public String getPalabraActual() { return palabraActual; }
    public void setPalabraActual(String palabraActual) { this.palabraActual = palabraActual; }

    public List<String> getJugadores() { return jugadores; }
    public void setJugadores(List<String> jugadores) { this.jugadores = jugadores; }

    public boolean isPartidaTerminada() { return partidaTerminada; }
    public void setPartidaTerminada(boolean partidaTerminada) { this.partidaTerminada = partidaTerminada; }
}