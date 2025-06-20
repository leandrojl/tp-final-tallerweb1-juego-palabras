package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.*;

import java.util.List;


public interface PartidaService {

    Partida2 crearPartida(String nombre, String idioma, boolean permiteComodin,
                          int rondasTotales, int minimoJugadores, int maximoJugadores);

    boolean unirseAPartida(Long partidaId, String jugadorId, Jugador jugador);

    boolean iniciarPartida(Long partidaId);

    ResultadoIntento procesarIntento(Long partidaId, String jugadorId, String intento, int tiempoRestante);

    void finalizarRondaPorTiempo(Long partidaId);

    EstadoPartida obtenerEstadoPartida(Long partidaId);

}
