package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;

import jakarta.persistence.*;

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
    private Integer puntaje;
    private boolean gano;
    private Estado estado;

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

    public Integer getPuntaje() {
        return puntaje;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public boolean isGano() {
        return gano;
    }

    public void setGano(boolean gano) {
        this.gano = gano;
    }

    public void setPuntaje(Integer puntaje) {
        this.puntaje = puntaje;
    }
}