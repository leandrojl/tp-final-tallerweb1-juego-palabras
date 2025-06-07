package com.tallerwebi.dominio.model;

import jdk.jfr.MemoryAddress;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class UsuarioPartida {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    private Long id_usuario;
    @ManyToMany
    private Long id_partida;


}

