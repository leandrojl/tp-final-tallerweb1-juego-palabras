package com.tallerwebi.dominio.model;

import com.tallerwebi.dominio.Enum.Tipo_Usuario;

import javax.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String fotoPerfil;
    private String nombreUsuario ="";
    private int moneda;
    private String password;
    @Enumerated(EnumType.STRING)
    private Tipo_Usuario rol;

    public Usuario() {

    }
    public Usuario(String nombreUsuario, String email, String passoword, int moneda){
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = passoword;
        this.moneda = moneda;
}

    public Usuario(String nombreUsuario, String email, String password) {
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.password = password;
    }



    public Usuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
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