package com.tallerwebi.dominio.DTO;

public class ResultadoIntentoDto {
    private boolean esCorrecto;
    private String jugador;
    private String palabraCorrecta;
    private String palabraIncorrecta;

    public ResultadoIntentoDto(boolean esCorrecto, String jugador, String palabraCorrecta, String palabraIncorrecta, String mensaje, String mensajeIncorrecto) {
        this.esCorrecto = esCorrecto;
        this.jugador = jugador;
        this.palabraCorrecta = palabraCorrecta;
        this.palabraIncorrecta = palabraIncorrecta;
    }

    public ResultadoIntentoDto() {
    }

    public Boolean getEsCorrecto() {
        return esCorrecto;
    }

    public String getJugador() {
        return jugador;
    }

    public String getPalabraCorrecta() {
        return palabraCorrecta;
    }

    public String getPalabraIncorrecta() {
        return palabraIncorrecta;
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

    public void setPalabraIncorrecta(String intentoTexto) {
        this.palabraIncorrecta = intentoTexto;
    }

}
