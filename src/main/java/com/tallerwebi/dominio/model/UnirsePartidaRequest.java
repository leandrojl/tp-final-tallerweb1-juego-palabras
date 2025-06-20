package com.tallerwebi.dominio.model;

public class UnirsePartidaRequest  {
    private Long partidaId;
    private String nombreJugador;

    // Getters y setters
    public Long getPartidaId() { return partidaId; }
    public void setPartidaId(Long partidaId) { this.partidaId = partidaId; }
    public String getNombreJugador() { return nombreJugador; }
    public void setNombreJugador(String nombreJugador) { this.nombreJugador = nombreJugador; }
}
