package com.tallerwebi.dominio.model;

import org.hibernate.annotations.Generated;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Palabra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "palabra_id") // Clave foránea en la tabla Definicion
    private List<Definicion> definiciones = new ArrayList<>();

    public Palabra() {

    }

    public List<Definicion> getDefinicion() {
        return definiciones;
    }

    public void setDefinicion(List<Definicion> definiciones) {
        this.definiciones = definiciones;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }}