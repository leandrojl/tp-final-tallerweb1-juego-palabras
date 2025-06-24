package com.tallerwebi.dominio;

public class RankingDTO {
    private String nombreUsuario;
    private int puntaje;

    public RankingDTO(String nombreUsuario, int puntaje) {
        this.nombreUsuario = nombreUsuario;
        this.puntaje = puntaje;
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
}
