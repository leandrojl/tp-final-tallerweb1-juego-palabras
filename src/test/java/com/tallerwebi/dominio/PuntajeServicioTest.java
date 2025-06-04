package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Jugador;
import com.tallerwebi.dominio.PuntajeServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PuntajeServicioTest {

    private PuntajeServicioImpl puntajeServicio;

    @BeforeEach
    void setUp() {
        puntajeServicio = new PuntajeServicioImpl();
    }

    @Test
    void quePuedaRegistrarUnJugador() {
        Jugador jugador = new Jugador("gian", "Gian", "gian@hotmail.com", "1234");
        puntajeServicio.registrarJugador("gian", jugador);

        Map<Jugador, Integer> puntajes = puntajeServicio.obtenerTodosLosPuntajes();
        assertEquals(1, puntajes.size());
        assertTrue(puntajes.keySet().stream().anyMatch(j -> j.getNombre().equals("Gian")));
    }

    @Test
    void quePuedaSumarPuntosACadaJugador() {
        Jugador jugador = new Jugador("gian", "Gian", "gian@mail.com", "1234");
        puntajeServicio.registrarJugador("gian", jugador);

        puntajeServicio.registrarPuntos("gian", 100);
        puntajeServicio.registrarPuntos("gian", 50);

        int puntaje = puntajeServicio.obtenerPuntaje("gian");
        assertEquals(150, puntaje);
    }

    @Test
    void queRetorneCeroSiJugadorNoTienePuntos() {
        Jugador jugador = new Jugador("lea", "Lea", "lea@hotmail.com", "1234");
        puntajeServicio.registrarJugador("lea", jugador);

        int puntaje = puntajeServicio.obtenerPuntaje("lea");
        assertEquals(0, puntaje);
    }

    @Test
    void queDevuelvaRankingOrdenadoPorPuntajeDesc() {
        Jugador j1 = new Jugador("july3p", "July", "july3p@hotmail.com", "1234");
        Jugador j2 = new Jugador("lea", "Lea", "lea@hotmail.com", "1234");
        Jugador j3 = new Jugador("ana", "Ana", "ana@hotmail.com", "1234");

        puntajeServicio.registrarJugador("july3p", j1);
        puntajeServicio.registrarJugador("lea", j2);
        puntajeServicio.registrarJugador("ana", j3);

        puntajeServicio.registrarPuntos("lea", 200);
        puntajeServicio.registrarPuntos("ana", 100);
        puntajeServicio.registrarPuntos("july3p", 300);

        List<Map.Entry<Jugador, Integer>> ranking = puntajeServicio.obtenerTodosLosPuntajes()
                .entrySet().stream()
                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        assertEquals("July", ranking.get(0).getKey().getNombre());
        assertEquals("Lea", ranking.get(1).getKey().getNombre());
        assertEquals("Ana", ranking.get(2).getKey().getNombre());
    }
}
