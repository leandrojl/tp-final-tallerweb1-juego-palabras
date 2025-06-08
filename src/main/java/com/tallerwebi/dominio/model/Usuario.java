package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.Enum.Tipo_Usuario;

import javax.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String fotoPerfil;
    private String nombre;
    private int moneda;
    private String password;
    private Tipo_Usuario rol;

    public Usuario() {}


    public Usuario(String pepe1235421, String mail, String abc123245) {
        this.email = mail;
        this.fotoPerfil = pepe1235421;
        this.nombre = abc123245;
        this.moneda = 0; // Inicializamos moneda a 0
        this.password = "defaultPassword"; // Asignar una contraseña por defecto
        this.rol = Tipo_Usuario.JUGADOR; // Asignar un rol por defecto
    }

    public Usuario(String messi) {
        this.nombre = messi;
        this.email = "";
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getMoneda() {
        return moneda;
    }

    public void setMoneda(int moneda) {
        this.moneda = moneda;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Tipo_Usuario getRol() {
        return rol;
    }
    public void setRol(Tipo_Usuario rol) {
        this.rol = rol;
    }



}