package com.tallerwebi.dominio.model;

public class MensajeRecibidoDTO {

    private String message;
    private Long number;

    public MensajeRecibidoDTO() {
    }

    public MensajeRecibidoDTO(String message, Long number) {
        this.message = message;
        this.number = number;
    }

    public MensajeRecibidoDTO(String message) {
        this.message = message;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getNumber() {
        return number;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
