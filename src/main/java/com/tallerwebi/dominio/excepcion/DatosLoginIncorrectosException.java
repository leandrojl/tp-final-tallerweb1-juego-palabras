package com.tallerwebi.dominio.excepcion;

public class DatosLoginIncorrectosException extends RuntimeException {
    public DatosLoginIncorrectosException(String mensaje) {
        super(mensaje);
    }
}
