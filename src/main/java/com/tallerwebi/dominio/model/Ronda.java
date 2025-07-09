package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Ronda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Partida partida;
    @OneToOne
    private Palabra palabra;
    private int numeroDeRonda;
    @Enumerated(EnumType.STRING)
    private Estado estado;
    private LocalDateTime fechaHora; //LocalDateTime es una clase que se mapea a datetime con huibernate, con .now() genera el horario actual.

    public Ronda(Partida partida) {
        this.partida = partida;
    }

    public Ronda() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partida getPartida() {
        return partida;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
    }

    public Palabra getPalabra() {
        return palabra;
    }

    public void setPalabra(Palabra palabra) {
        this.palabra = palabra;
    }

    public int getNumeroDeRonda() {
        return numeroDeRonda;
    }

    public void setNumeroDeRonda(int numeroDeRonda) {
        this.numeroDeRonda = numeroDeRonda;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }


}