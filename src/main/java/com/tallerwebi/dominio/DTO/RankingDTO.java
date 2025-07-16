package com.tallerwebi.dominio.DTO;

public class RankingDTO {
    private String nombreUsuario;
    private int puntaje;
    private boolean gano;

    public RankingDTO(String nombreUsuario, int puntaje, boolean gano) {
        this.nombreUsuario = nombreUsuario;
        this.puntaje = puntaje;
        this.gano = gano;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public boolean getGano() {
        return gano;
    }
    public void setGano(boolean gano) {
        this.gano = gano;
    }
}
