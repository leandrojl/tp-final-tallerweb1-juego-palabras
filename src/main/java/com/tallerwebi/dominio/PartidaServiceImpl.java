package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PartidaServiceImpl implements PartidaService {
    private final Map<String, Partida2> partidas = new HashMap<>();

    @Override
    public Partida2 iniciarNuevaPartida(String jugadorId, String nombre) {
        Partida2 nueva = new Partida2();
      //  nueva.agregarJugador(jugadorId, nombre);
        partidas.put(jugadorId, nueva);
        return nueva;
    }

    @Override
    public Partida2 obtenerPartida(String jugadorId) {
        return partidas.get(jugadorId);
    }

    @Override
    public void eliminarPartida(String jugadorId) {
        partidas.remove(jugadorId);
    }


}
