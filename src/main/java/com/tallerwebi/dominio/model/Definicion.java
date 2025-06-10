package com.tallerwebi.dominio.model;
import jakarta.persistence.*;



@Entity
public class Definicion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String definicion;


    public Definicion() {

    }

    public Definicion(String def) {
        this.definicion = def;
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
}
