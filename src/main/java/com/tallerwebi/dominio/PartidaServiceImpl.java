package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PartidaServiceImpl implements PartidaService {
    private final Map<String, Partida> partidas = new HashMap<>();

    @Override
    public Partida iniciarNuevaPartida(String jugadorId, String nombre) {
        Partida nueva = new Partida();
        nueva.agregarJugador(jugadorId, nombre);
        partidas.put(jugadorId, nueva);
        return nueva;
    }

    @Override
    public Partida obtenerPartida(String jugadorId) {
        return partidas.get(jugadorId);
    }

    @Override
    public void eliminarPartida(String jugadorId) {
        partidas.remove(jugadorId);
    }

    @Override
    public List<Partida> obtenerPartidasDisponibles() {
        List<Partida> partidas = new ArrayList<>(List.of(
                new Partida("Partida de prueba"),
                new Partida("Partida de prueba 2"),
                new Partida("Partida de prueba 3")
        ));
        return partidas;
    }
}
