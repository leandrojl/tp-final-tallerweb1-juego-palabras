package com.tallerwebi.dominio;

public class DtoIntento {
    private Long partidaId;
    private String intentoTexto; // lo que escribió el jugador


    public DtoIntento() {

    }

    public DtoIntento(Long partidaId, String intentoTexto) {
        this.partidaId = partidaId;
        this.intentoTexto = intentoTexto;
    }

    public String getIntentoTexto() {
        return intentoTexto;
    }

    public Long getPartidaId() {
        return partidaId;
    }

    public void setPartidaId(Long partidaId) {
        this.partidaId = partidaId;
    }

    public void setIntentoTexto(String intentoTexto) {
        this.intentoTexto = intentoTexto;
    }
}
