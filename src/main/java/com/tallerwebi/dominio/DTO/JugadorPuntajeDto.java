package com.tallerwebi.dominio.DTO;

public class JugadorPuntajeDto {
    private String nombre;
    private int puntaje;


    public JugadorPuntajeDto(){

    }

    // Constructor
    public JugadorPuntajeDto(String nombre, int puntaje) {
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    // Getters y setters


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }
}
