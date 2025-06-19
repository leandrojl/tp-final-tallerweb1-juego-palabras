package com.tallerwebi.dominio.model;

import java.util.ArrayList;
import java.util.List;

public class ListaUsuarios {
    private List<String> usuarios;

    public ListaUsuarios() {
    }

    public ListaUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }

    public List<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }
}
