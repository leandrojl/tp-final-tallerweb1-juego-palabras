package com.tallerwebi.dominio.model;

import java.util.List;

public class ListaUsuariosDTO {
    private List<String> usuarios;

    public ListaUsuariosDTO() {
    }

    public ListaUsuariosDTO(List<String> usuarios) {
        this.usuarios = usuarios;
    }

    public List<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }
}
