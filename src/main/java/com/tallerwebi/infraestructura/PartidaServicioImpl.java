package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.PartidaServicio;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PartidaServicioImpl implements PartidaServicio {
    private Map<String, Integer> jugadores;
    private int rondaActual;
    private boolean partidaTerminada;
    private String palabraActual;
    private String definicionActual;

    private static final int MAX_RONDAS = 5;

    public PartidaServicioImpl() {
        this.jugadores = new HashMap<>();
        this.rondaActual = 0;
        this.partidaTerminada = false;
    }

    @Override
    public void agregarJugador(String jugadorId) {
        jugadores.putIfAbsent(jugadorId, 0);
    }

    @Override
    public void actualizarPuntos(String jugadorId, int puntos) {
        jugadores.put(jugadorId, jugadores.getOrDefault(jugadorId, 0) + puntos);
    }

    /**
     * Avanza la ronda y actualiza palabra y definición.
     * @return true si hay siguiente ronda, false si terminó.
     */
    @Override
    public boolean avanzarRonda(String nuevaPalabra, String nuevaDefinicion) {
        if (rondaActual < MAX_RONDAS) {
            rondaActual++;
            this.palabraActual = nuevaPalabra;
            this.definicionActual = nuevaDefinicion;
            return true;
        } else {
            partidaTerminada = true;
            return false;
        }
    }

    // Getters y setters
    @Override
    public int getRondaActual() { return rondaActual; }
    @Override
    public boolean isPartidaTerminada() { return partidaTerminada; }
    @Override
    public String getPalabraActual() { return palabraActual; }
    @Override
    public String getDefinicionActual() { return definicionActual; }
    @Override
    public Integer getPuntaje(String jugadorId) { return jugadores.getOrDefault(jugadorId, 0); }
}


