package com.tallerwebi.dominio.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
@Entity
public class Palabra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    private String idioma; // 👈 ESTE FALTABA

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "palabra_id")
    private List<Definicion> definiciones = new ArrayList<>();

    public Palabra() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public List<Definicion> getDefinicion() {
        return definiciones;
    }

    public void setDefinicion(List<Definicion> definiciones) {
        this.definiciones = definiciones;
    }
}
