package com.tallerwebi.dominio;

public interface PartidaServicio {
    void agregarJugador(String jugadorId);

    void actualizarPuntos(String jugadorId, int puntos);

    boolean avanzarRonda(String nuevaPalabra, String nuevaDefinicion);
}
