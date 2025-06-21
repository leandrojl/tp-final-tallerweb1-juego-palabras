package com.tallerwebi.dominio.model;

public class MensajeRecibidoDTO {

    private String message;

    public MensajeRecibidoDTO() {
    }

    public MensajeRecibidoDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
