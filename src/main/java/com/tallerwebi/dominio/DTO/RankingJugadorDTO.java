package com.tallerwebi.dominio.DTO;

public class RankingJugadorDTO {
    private String nombreUsuario;
    private int partidasJugadas;
    private int partidasGanadas;
    private double winrate;

    public RankingJugadorDTO(String nombreUsuario, int partidasJugadas, int partidasGanadas, double winrate) {
        this.nombreUsuario = nombreUsuario;
        this.partidasJugadas = partidasJugadas;
        this.partidasGanadas = partidasGanadas;
        this.winrate = winrate;
    }

    // Getters y setters
    public RankingJugadorDTO() {
    }
public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public int getPartidasGanadas() {
        return partidasGanadas;
    }

    public void setPartidasGanadas(int partidasGanadas) {
        this.partidasGanadas = partidasGanadas;
    }

    public double getWinrate() {
        return winrate;
    }

    public void setWinrate(double winrate) {
        this.winrate = winrate;
    }
}

