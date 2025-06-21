package com.tallerwebi.dominio;

public class DtoIntento {
    private Long partidaId;
    private String intentoTexto; // lo que escribió el jugador


    public String getIntentoTexto() {
        return intentoTexto;
    }

    public Long getPartidaId() {
        return partidaId;
    }
}
