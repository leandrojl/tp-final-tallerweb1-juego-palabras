package com.tallerwebi.dominio.DTO;

public class DtoIntento {
    // lo que escribió el jugador
    private String intentoPalabra;
    private Long idUsuario;
    private Long idPartida;
    private int tiempoRestante;

    public DtoIntento(String intentoPalabra, Long usuarioId, Long partidaId, int tiempoRestante) {
        this.intentoPalabra = intentoPalabra;
        this.idUsuario = usuarioId;
        this.idPartida = partidaId;
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Long partidaId) {
        this.idPartida = partidaId;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }
}
