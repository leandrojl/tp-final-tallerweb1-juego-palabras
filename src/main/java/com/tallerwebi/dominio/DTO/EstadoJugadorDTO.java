package com.tallerwebi.dominio.DTO;

public class EstadoJugadorDTO {
    private Long idPartida;
    private String username;
    private boolean estaListo;

    public EstadoJugadorDTO(Long idPartida,String username, boolean estaListo) {
        this.idPartida = idPartida;
        this.username = username;
        this.estaListo = estaListo;
    }

    public EstadoJugadorDTO() {

    }

    public Long getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Long idPartida) {
        this.idPartida = idPartida;
    }

    // Getters y setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEstaListo() {
        return estaListo;
    }

    public void setEstaListo(boolean estaListo) {
        this.estaListo = estaListo;
    }
}
