package com.tallerwebi.dominio.model;

public class MensajeRecibido {

    private String message;

    public MensajeRecibido() {
    }

    public MensajeRecibido(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
