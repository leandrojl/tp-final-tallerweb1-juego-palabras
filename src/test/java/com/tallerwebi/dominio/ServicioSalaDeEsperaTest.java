package com.tallerwebi.dominio;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServicioSalaDeEsperaTest {

    //se prueba la implementacion del ServicioSalaDeEspera(interfaz)
    private final ServicioSalaDeEspera servicioSalaDeEspera = new ServicioSalaDeEsperaImpl();

    @Test
    public void deberiaObtenerJugadoresDelFormularioCorrectamente() {
        // dado que tengo un mapa que viene del formulario de la sala de espera
        Map<String, String> parametros = Map.of(
                "jugador_1", "true",
                "jugador_2", "false",
                "otro_parametro", "irrelevante"
        );

        // cuando obtengo los jugadores del formulario y parseo la llave-valor
        Map<Long, Boolean> resultado = servicioSalaDeEspera.obtenerJugadoresDelFormulario(parametros);

        // entonces obtengo los jugadores con su llave-valor
        assertEquals(2, resultado.size());
        assertEquals(true, resultado.get(1L));
        assertEquals(false, resultado.get(2L));
    }

    @Test
    public void deberiaRetornarJugadoresNoListos() {
        // Datos de entrada simulados
        Map<Long, Boolean> jugadores = Map.of(
                1L, true,
                2L, false,
                3L, true,
                4L, false
        );

        // Llamada al método
        List<Long> resultado = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);

        // Verificaciones
        assertEquals(2, resultado.size());
        assertEquals(List.of(2L, 4L), resultado);
    }

    @Test
    public void deberiaRetornarListaVaciaSiTodosEstanListos() {
        // dado un mapa de jugadores con todos listos
        Map<Long, Boolean> jugadores = Map.of(
                1L, true,
                2L, true,
                3L, true
        );

        // cuando verifico si los jugadores que no estan listos
        List<Long> resultado = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);

        // entonces todos los jugadores estan listos
        assertEquals(0, resultado.size());
    }

    @Test
    public void deberiaRetornarListaVaciaSiNoHayJugadores() {
        // dado que no tengo jugadores
        Map<Long, Boolean> jugadores = Map.of();

        // cuando verifico los jugadores que no estan listos
        List<Long> resultado = servicioSalaDeEspera.verificarSiHayJugadoresQueNoEstenListos(jugadores);

        // entonces no hay jugadores listos
        assertEquals(0, resultado.size());
    }
}
