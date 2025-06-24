package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.model.Partida2;

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
    private boolean gano;
    private int puntaje;

    public UsuarioPartida (){

    }


    public UsuarioPartida(Usuario usuario, Partida2 partida, boolean gano) {
    this.usuario=usuario;
    this.partida=partida;
    this.gano=gano;
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

    public Partida2 getPartida() {
        return partida;
    }

    public void setPartida(Partida2 partida) {
        this.partida = partida;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public boolean isGano() {
        return gano;
    }

    public int getPuntaje() {
        return puntaje;
    }
}