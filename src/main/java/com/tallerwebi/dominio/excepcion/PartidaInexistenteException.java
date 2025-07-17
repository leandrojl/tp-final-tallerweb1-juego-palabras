package com.tallerwebi.dominio.excepcion;

public class PartidaInexistenteException extends RuntimeException {
    public PartidaInexistenteException(String mensaje) {
        super(mensaje);
    }
}
