package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;

import javax.persistence.*;
import java.util.*;
@Entity
public class Partida {

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

    public Partida() {

    }


    public Partida(String nombre, String idioma, boolean permiteComodin, int rondasTotales, int maximoJugadores, int minimoJugadores, Estado estado) {
        this.nombre = nombre;
        this.idioma = idioma;
        this.permiteComodin = permiteComodin;
        this.rondasTotales = rondasTotales;
        this.maximoJugadores = maximoJugadores;
        this.minimoJugadores = minimoJugadores;
        this.estado = estado;
    }

    public Partida(String nombre, String idioma, boolean permiteComodin, int rondasTotales, int minimoJugadores) {
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


    public Arrays getUsuariosPartida() {
        return null;
    }

    public Object getRondaActual() {return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Partida partida = (Partida) o;
        return permiteComodin == partida.permiteComodin && rondasTotales == partida.rondasTotales && maximoJugadores == partida.maximoJugadores && minimoJugadores == partida.minimoJugadores && Objects.equals(id, partida.id) && Objects.equals(nombre, partida.nombre) && Objects.equals(idioma, partida.idioma) && estado == partida.estado;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, idioma, permiteComodin, rondasTotales, maximoJugadores, minimoJugadores, estado);
    }
}
