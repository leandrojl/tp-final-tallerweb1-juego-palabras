package com.tallerwebi.dominio.DTO;

public class BloqueoDto {
    private String usuarioBloqueado;
    private int tiempoBloqueo; // en segundos
    private String mensaje;

    public BloqueoDto() {}

    public BloqueoDto(String usuarioBloqueado, int tiempoBloqueo, String mensaje) {
        this.usuarioBloqueado = usuarioBloqueado;
        this.tiempoBloqueo = tiempoBloqueo;
        this.mensaje = mensaje;
    }

    // getters y setters
    public String getUsuarioBloqueado() { return usuarioBloqueado; }
    public void setUsuarioBloqueado(String usuarioBloqueado) { this.usuarioBloqueado = usuarioBloqueado; }

    public int getTiempoBloqueo() { return tiempoBloqueo; }
    public void setTiempoBloqueo(int tiempoBloqueo) { this.tiempoBloqueo = tiempoBloqueo; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
