package com.tallerwebi.dominio.DTO;

public class MensajeEnviadoDTO {
    private String username;
    private String content;

    public MensajeEnviadoDTO() {
    }

    public MensajeEnviadoDTO(String content) {
        this.content = content;
    }


    public MensajeEnviadoDTO(String nombreUsuario, String content) {
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
