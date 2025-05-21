package com.tallerwebi.dominio;

public interface PartidaServicio {
    void agregarJugador(String jugadorId);

    void actualizarPuntos(String jugadorId, int puntos);

    boolean avanzarRonda(String nuevaPalabra, String nuevaDefinicion);

    // Getters y setters
    int getRondaActual();

    boolean isPartidaTerminada();

    String getPalabraActual();

    String getDefinicionActual();

    Integer getPuntaje(String jugadorId);
}
