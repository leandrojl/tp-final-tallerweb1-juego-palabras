package com.tallerwebi.dominio.model;

public class Jugador {
    private  String nombre;
    private  String usuario;
    private  String email;
    private  String password;

    public Jugador(){

    }

    public Jugador(String usuario, String nombre, String email, String password) {
        this.usuario = usuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
    }

    public Jugador(String nombre) {
        this.nombre = nombre;
    }


    public String getUsuario() {
        return usuario;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setUsuario(String usuario){
        this.usuario = usuario;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }
}

