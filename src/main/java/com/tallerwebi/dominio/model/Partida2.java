package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Partida2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String idioma;
    private boolean permiteComodin;
    private int rondasTotales;
    private int maximoJugadores;
    private int minimoJugadores;
    @Enumerated(EnumType.STRING)
    private Estado estado;

    public Partida2(){

    }

    public Partida2(String nombre, String idioma, boolean permiteComodin, int rondasTotales,int maximoJugadores, int minimoJugadores, Estado estado) {
        this.nombre = nombre;
        this.idioma = idioma;
        this.permiteComodin = permiteComodin;
        this.rondasTotales = rondasTotales;
        this.maximoJugadores = maximoJugadores;
        this.minimoJugadores = minimoJugadores;
        this.estado = estado;
    }

    public Partida2(String nombre, String idioma, boolean permiteComodin, int rondasTotales, int minimoJugadores) {
        this.nombre = nombre;
        this.idioma = idioma;
        this.permiteComodin = permiteComodin;
        this.rondasTotales = rondasTotales;
        this.minimoJugadores = minimoJugadores;
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

    public void setMinimoJugadores(int minimoJugadores) {
        this.minimoJugadores = minimoJugadores;
    }

    public int getMaximoJugadores() {
        return maximoJugadores;
    }

    public void setMaximoJugadores(int maximoJugadores) {
        this.maximoJugadores = maximoJugadores;
    }

    public int getMinimoJugadores() {
        return minimoJugadores;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partida2 partida2 = (Partida2) o;
        return permiteComodin == partida2.permiteComodin &&
                rondasTotales == partida2.rondasTotales &&
                minimoJugadores == partida2.minimoJugadores &&
                Objects.equals(nombre, partida2.nombre) &&
                Objects.equals(idioma, partida2.idioma) &&
                estado == partida2.estado;
    }
}