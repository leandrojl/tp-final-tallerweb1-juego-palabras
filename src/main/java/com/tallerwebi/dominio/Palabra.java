package com.tallerwebi.dominio;

import org.hibernate.annotations.Generated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Palabra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    private List<String> definicion;

    public List<String> getDefinicion() {
        return definicion;
    }

    public void setDefinicion(List<String> definicion) {
        this.definicion = definicion;
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