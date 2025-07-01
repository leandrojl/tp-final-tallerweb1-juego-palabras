package com.tallerwebi.dominio.model;

public class MensajeInicioRonda {
    private Long partidaId;
    public MensajeInicioRonda(Long partidaId){
        this.partidaId=partidaId;
    }
    public MensajeInicioRonda(){};

    public Long getPartidaId() { return partidaId; }
    public void setPartidaId(Long partidaId) { this.partidaId = partidaId; }
}
