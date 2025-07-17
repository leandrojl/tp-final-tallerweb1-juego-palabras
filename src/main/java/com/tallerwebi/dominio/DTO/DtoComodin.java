package com.tallerwebi.dominio.DTO;

public class DtoComodin {

    private Long idPartida;
    private Long idUsuario;
    private boolean letraRepetida;
    public DtoComodin() {
    }

    public Long getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Long idPartida) {
        this.idPartida = idPartida;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean isLetraRepetida() {
        return letraRepetida;
    }

    public void setLetraRepetida(boolean letraRepetida) {
        this.letraRepetida = letraRepetida;
    }
}
