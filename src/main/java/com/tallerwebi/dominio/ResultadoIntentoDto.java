package com.tallerwebi.dominio;

public class ResultadoIntentoDto {
    private boolean esCorrecto;
    private String jugador;
    private String palabraCorrecta;

    public boolean isEsCorrecto() {
        return esCorrecto;
    }

    public String getJugador() {
        return jugador;
    }

    public String getPalabraCorrecta() {
        return palabraCorrecta;
    }

    public void setCorrecto(boolean esCorrecto) {
        this.esCorrecto = esCorrecto;
    }

    public void setJugador(String nombreJugador) {
        this.jugador = nombreJugador;
    }

    public void setPalabraCorrecta(String descripcion) {
        this.palabraCorrecta = descripcion;
    }
}
