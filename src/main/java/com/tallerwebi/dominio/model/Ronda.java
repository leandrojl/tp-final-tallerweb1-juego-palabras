package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Ronda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Partida2 partida;
    @OneToOne
    private String palabra;
    private int numeroDeRonda;
    private Estado estado;
    private LocalDateTime fechaHora; //LocalDateTime es una clase que se mapea a datetime con huibernate, con .now() genera el horario actual.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Partida2 getPartida() {
        return partida;
    }

    public void setPartida(Partida2 partida) {
        this.partida = partida;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
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

    public void setDefinicion(Definicion definicionAleatoria) {
    }
}