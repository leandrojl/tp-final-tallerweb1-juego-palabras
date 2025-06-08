package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Partida2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String idioma;
    private boolean permiteComodin;
    private int rondasTotales;
    private int minimoJugadores;
    private Estado estado;

    public Partida2(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public boolean isPermiteComodin() {
        return permiteComodin;
    }

    public void setPermiteComodin(boolean permiteComodin) {
        this.permiteComodin = permiteComodin;
    }

    public int getRondasTotales() {
        return rondasTotales;
    }

    public void setRondasTotales(int rondasTotales) {
        this.rondasTotales = rondasTotales;
    }

    public int getMinimoJugadores() {
        return minimoJugadores;
    }

    public void setMinimoJugadores(int minimoJugadores) {
        this.minimoJugadores = minimoJugadores;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}