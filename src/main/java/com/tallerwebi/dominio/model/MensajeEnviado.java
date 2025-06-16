package com.tallerwebi.dominio.model;

public class MensajeEnviado {
    private String username;
    private String content;

    public MensajeEnviado() {
    }

    public MensajeEnviado(String content) {
        this.content = content;
    }


    public MensajeEnviado(String nombreUsuario, String content) {
        this.content = content;
        this.username = nombreUsuario;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
