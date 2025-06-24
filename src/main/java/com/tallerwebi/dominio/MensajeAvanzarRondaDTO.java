package com.tallerwebi.dominio;

public class MensajeAvanzarRondaDTO {
    private Long idPartida;
    private Long idRonda;
    private boolean tiempoAgotado;


    public MensajeAvanzarRondaDTO(Long idPartida, Long idRonda, boolean tiempoAgotado) {
        this.idPartida = idPartida;
        this.idRonda = idRonda;
        this.tiempoAgotado = tiempoAgotado;
    }

    public Long getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(Long idPartida) {
        this.idPartida = idPartida;
    }

    public Long getIdRonda() {
        return idRonda;
    }

    public void setIdRonda(Long idRonda) {
        this.idRonda = idRonda;
    }

    public boolean isTiempoAgotado() {
        return tiempoAgotado;
    }

    public void setTiempoAgotado(boolean tiempoAgotado) {
        this.tiempoAgotado = tiempoAgotado;
    }
}
