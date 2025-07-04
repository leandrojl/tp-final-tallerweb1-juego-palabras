package com.tallerwebi.dominio.excepcion;

public class PartidaAleatoriaNoDisponibleException extends RuntimeException {
    public PartidaAleatoriaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
