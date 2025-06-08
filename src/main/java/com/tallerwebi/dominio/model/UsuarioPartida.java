package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Partida2;

import javax.persistence.*;

@Entity
public class UsuarioPartida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_partida", nullable = false)
    private Partida2 partida;

    public UsuarioPartida (){

    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Partida2 getPartida() {
        return partida;
    }

    public void setPartida(Partida2 partida) {
        this.partida = partida;
    }
}