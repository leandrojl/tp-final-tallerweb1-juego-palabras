package com.tallerwebi.dominio.excepcion;

public class CantidadDeUsuariosInsuficientesException extends RuntimeException {
    public CantidadDeUsuariosInsuficientesException(String message) {
        super(message);
    }
}
