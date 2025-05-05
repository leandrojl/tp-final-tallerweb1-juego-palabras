package com.tallerwebi.dominio;

public class Jugador {
    private String usuario;
    private String email;
    private String password;

    public Jugador() {

    }

    public Jugador(String usuario, String email, String password) {
        this.usuario = usuario;
        this.email = email;
        this.password = password;
    }

    public String getUsuario() {
        return this.usuario;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
