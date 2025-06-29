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
    private Estado estado;


    public UsuarioPartida (){

    }

    public UsuarioPartida(Usuario usuario, Partida partida, int puntaje, boolean gano, Estado estado) {
        this.usuario = usuario;
        this.partida = partida;
        this.puntaje = puntaje;
        this.gano = gano;
        this.estado = estado;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    // Getters y setters
    public boolean getGano() {
        return gano;
    }

    public void setGano(boolean gano) {
        this.gano = gano;
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

    public boolean isGano() {
        return gano;
    }
}