package com.tallerwebi.dominio.model;

import javax.persistence.*;

@Entity
public class Acierto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_id_ronda", nullable = false)
    private Ronda ronda;

    @ManyToOne
    @JoinColumn(name = "fk_id_usuario", nullable = false)
    private Usuario usuario;

    private int ordenDeAcierto;

    public Acierto(){

    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ronda getRonda() {
        return ronda;
    }

    public void setRonda(Ronda ronda) {
        this.ronda = ronda;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getOrdenDeAcierto() {
        return ordenDeAcierto;
    }

    public void setOrdenDeAcierto(int ordenDeAcierto) {
        this.ordenDeAcierto = ordenDeAcierto;
    }
}