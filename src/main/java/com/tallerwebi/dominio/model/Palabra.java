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
    private String idioma;

    private String descripcion;

    @OneToMany(mappedBy = "palabra", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public List<Definicion> getDefiniciones() {
        return definiciones;
    }

    public void setDefiniciones(List<Definicion> definiciones) {
        this.definiciones = definiciones;
    }

    public void agregarDefinicion(Definicion definicion) {
        definiciones.add(definicion);
        definicion.setPalabra(this);
    }

    public void quitarDefinicion(Definicion definicion) {
        definiciones.remove(definicion);
        definicion.setPalabra(null);
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getIdioma() {
        return idioma;
    }
}
