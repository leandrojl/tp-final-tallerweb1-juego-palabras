package com.tallerwebi.dominio.model;

public class MensajeInicioRonda {
    private Long idPartida;
    public MensajeInicioRonda(Long idPartida){
        this.idPartida = idPartida;
    }
    public MensajeInicioRonda(){};

    public Long getIdPartida() { return idPartida; }
    public void setIdPartida(Long idPartida) { this.idPartida = idPartida; }
}
