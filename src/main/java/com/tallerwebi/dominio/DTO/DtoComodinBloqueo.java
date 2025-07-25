package com.tallerwebi.dominio.DTO;

public class DtoComodinBloqueo {
    private Long idPartida;
    private Long idUsuario;
    private String usuarioABloquear;

    // constructores, getters y setters
    public DtoComodinBloqueo() {}

    public DtoComodinBloqueo(Long idPartida, Long idUsuario, String usuarioABloquear) {
        this.idPartida = idPartida;
        this.idUsuario = idUsuario;
        this.usuarioABloquear = usuarioABloquear;
    }

    // getters y setters
    public Long getIdPartida() { return idPartida; }
    public void setIdPartida(Long idPartida) { this.idPartida = idPartida; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getUsuarioABloquear() { return usuarioABloquear; }
    public void setUsuarioABloquear(String usuarioABloquear) { this.usuarioABloquear = usuarioABloquear; }
}