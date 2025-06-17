package com.tallerwebi.dominio.excepcion;

public class UsuarioInvalidoException extends RuntimeException {

    public UsuarioInvalidoException(String mensaje) {
        super(mensaje);
    }
}
