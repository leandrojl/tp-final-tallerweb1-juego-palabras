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
    @JoinColumn(name = "partida_id", nullable = false)
    private Partida2 partida;

    // CAMBIO AQUÍ: De @OneToOne a @ManyToOne
    @ManyToOne(optional = false)
    @JoinColumn(name = "palabra_id", nullable = false)
    private Palabra palabra;

    private String definicion;
    private int numeroDeRonda;
    private Estado estado;
    private LocalDateTime fechaHora;

    // Getters y setters permanecen igual...
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

    public void setDefinicion(String definicionAleatoria) {
        this.definicion = definicionAleatoria;
    }

    public String getDefinicion() {
        return definicion;
    }
}