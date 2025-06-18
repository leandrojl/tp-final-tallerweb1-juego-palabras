package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceService.PuntajeService;
import com.tallerwebi.dominio.model.Jugador;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PuntajeServiceImpl implements PuntajeService {

    private final Map<String, Integer> puntajes = new HashMap<>();
    private final Map<String, Jugador> jugadores = new HashMap<>();

    @Override
    public void registrarPuntos(String jugadorId, int puntos) {
        puntajes.put(jugadorId, puntajes.getOrDefault(jugadorId, 0) + puntos);
    }

    @Override
    public int obtenerPuntaje(String jugadorId) {
        return puntajes.getOrDefault(jugadorId, 0);
    }

    @Override
    public Map<Jugador, Integer> obtenerTodosLosPuntajes() {
        return jugadores.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getValue,
                        entry -> puntajes.getOrDefault(entry.getKey(), 0)
                ));
    }

    @Override
    public void registrarJugador(String jugadorId, Jugador jugador) {
        jugadores.putIfAbsent(jugadorId, jugador);
    }

}

