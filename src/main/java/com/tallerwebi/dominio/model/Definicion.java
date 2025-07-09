package com.tallerwebi.dominio.model;

import javax.persistence.*;


@Entity
public class Definicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String definicion;

    @ManyToOne
    @JoinColumn(name = "palabra_id") // clave foránea hacia Palabra
    private Palabra palabra;

    public Definicion() {

    }

    public Definicion(String definicion) {
        this.definicion = definicion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDefinicion() {
        return definicion;
    }

    public void setDefinicion(String definicion) {
        this.definicion = definicion;
    }

    public Palabra getPalabra() {
        return palabra;
    }

    public void setPalabra(Palabra palabra) {
        this.palabra = palabra;
    }


}
