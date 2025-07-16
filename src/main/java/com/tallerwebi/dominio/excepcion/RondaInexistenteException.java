package com.tallerwebi.dominio.excepcion;

public class RondaInexistenteException extends RuntimeException {
    public RondaInexistenteException(String mensaje) {
        super(mensaje);
    }
}
