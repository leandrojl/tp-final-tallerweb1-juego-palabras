package com.tallerwebi.presentacion;

public class DatosLogin {
    private String nombre;
    private String password;

    public DatosLogin() {
    }

    public DatosLogin(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

