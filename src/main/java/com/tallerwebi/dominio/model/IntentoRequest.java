package com.tallerwebi.dominio.model;

public class IntentoRequest {
    private Long partidaId;
    private String intento;
    private int tiempoRestante;

    // Getters y setters
    public Long getPartidaId() { return partidaId; }
    public void setPartidaId(Long partidaId) { this.partidaId = partidaId; }
    public String getIntento() { return intento; }
    public void setIntento(String intento) { this.intento = intento; }
    public int getTiempoRestante() { return tiempoRestante; }
    public void setTiempoRestante(int tiempoRestante) { this.tiempoRestante = tiempoRestante; }
}