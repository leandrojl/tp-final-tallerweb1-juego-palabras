package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;


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
    private Partida partida;
    private int puntaje;
    private boolean gano;
    @Enumerated(EnumType.STRING)
    private Estado estado;

    @Column(name = "comodin_usado")
    private boolean comodinUsado = false;

    public UsuarioPartida() {

    }


    public UsuarioPartida(Usuario usuario, Partida partida, int puntaje, boolean gano, Estado estado) {
        this.usuario = usuario;
        this.partida = partida;
        this.puntaje = puntaje;
        this.gano = gano;
        this.estado = estado;
    }

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

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public boolean isGano() {
        return gano;
    }

    public void setGano(boolean gano) {
        this.gano = gano;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean isComodinUsado() {
        return comodinUsado;
    }

    public void setComodinUsado(boolean comodinUsado) {
        this.comodinUsado = comodinUsado;
    }
}

