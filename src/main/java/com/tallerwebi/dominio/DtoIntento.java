package com.tallerwebi.dominio;

public class DtoIntento {
    // lo que escribió el jugador
    private String intentoPalabra;
    private Long usuarioId;
    private Long partidaId;
    private int tiempoRestante;

    public DtoIntento(String intentoPalabra, Long usuarioId, Long partidaId, int tiempoRestante) {
        this.intentoPalabra = intentoPalabra;
        this.usuarioId = usuarioId;
        this.partidaId = partidaId;
        this.tiempoRestante = tiempoRestante;
    }

    public DtoIntento(){

    }

    public String getIntentoPalabra() {
        return intentoPalabra;
    }

    public void setIntentoPalabra(String intentoPalabra) {
        this.intentoPalabra = intentoPalabra;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getPartidaId() {
        return partidaId;
    }

    public void setPartidaId(Long partidaId) {
        this.partidaId = partidaId;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }
}
