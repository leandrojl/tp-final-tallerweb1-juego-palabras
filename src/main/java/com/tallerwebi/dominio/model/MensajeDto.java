package com.tallerwebi.dominio.model;

public class MensajeDto {
    private Long idUsuario;
    private Long idPartida;
    private String texto;

    public MensajeDto() {

    }

    public MensajeDto(Long idUsuario,Long idPartida, String texto) {
        this.idUsuario = idUsuario;
        this.idPartida = idPartida;
        this.texto = texto;
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

    public void setIdPartida(Long idPartida) {
        this.idPartida = idPartida;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
