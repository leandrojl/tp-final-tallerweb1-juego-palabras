package com.tallerwebi.dominio;

public class Jugador {
    private final String usuario;
    private final String email;
    private final String password;

    public Jugador(String usuario, String email, String password) {
        this.usuario = usuario;
        this.email = email;
        this.password = password;
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
}
