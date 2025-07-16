// src/main/java/com/tallerwebi/dominio/DTO/CrearPartidaDTO.java
package com.tallerwebi.dominio.DTO;

public class CrearPartidaDTO {
    private String nombre;
    private String idioma;
    private boolean permiteComodin;
    private int rondasTotales;
    private int maximoJugadores;
    private int minimoJugadores;
    private boolean permiteBot;

    // Getters y Setters para todos los campos
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public boolean isPermiteComodin() { return permiteComodin; }
    public void setPermiteComodin(boolean permiteComodin) { this.permiteComodin = permiteComodin; }
    public int getRondasTotales() { return rondasTotales; }
    public void setRondasTotales(int rondasTotales) { this.rondasTotales = rondasTotales; }
    public int getMaximoJugadores() { return maximoJugadores; }
    public void setMaximoJugadores(int maximoJugadores) { this.maximoJugadores = maximoJugadores; }
    public int getMinimoJugadores() { return minimoJugadores; }
    public void setMinimoJugadores(int minimoJugadores) { this.minimoJugadores = minimoJugadores; }

    public boolean isPermiteBot() {
        return permiteBot;
    }

    public void setPermiteBot(boolean permiteBot) {
        this.permiteBot = permiteBot;
    }
}