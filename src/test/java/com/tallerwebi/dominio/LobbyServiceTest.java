package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class LobbyServiceTest {

    private LobbyService lobbyServiceMock;

    @BeforeEach
    void setUp() {
        lobbyServiceMock = mock(LobbyService.class);
    }

    @Test
    public void testObtenerPartidasEnEsperaConMock() {

        when(lobbyServiceMock.obtenerPartidasEnEspera()).thenReturn(List.of(
                new Partida("Partida en espera 1", "Ingles", true, 5,5, 2, Estado.EN_ESPERA),
                new Partida("Partida en espera 2", "Ingles", false, 3,5, 4, Estado.EN_ESPERA),
                new Partida("Partida en espera 3", "Ingles", true, 7,5, 3, Estado.EN_ESPERA)
        ));

        List<Partida> partidas = lobbyServiceMock.obtenerPartidasEnEspera();

        Assertions.assertEquals(3, partidas.size());
        Assertions.assertEquals("Partida en espera 1", partidas.get(0).getNombre());
        Assertions.assertEquals("Partida en espera 2", partidas.get(1).getNombre());
        Assertions.assertEquals("Partida en espera 3", partidas.get(2).getNombre());

        Assertions.assertEquals(Estado.EN_ESPERA, partidas.get(0).getEstado());
        Assertions.assertEquals(Estado.EN_ESPERA, partidas.get(1).getEstado());
        Assertions.assertEquals(Estado.EN_ESPERA, partidas.get(2).getEstado());

        verify(lobbyServiceMock, times(1)).obtenerPartidasEnEspera();
    }

    @Test
    public void queSePuedanObtenerPartidasDesdeElServicioMock() {

        // Dado que tengo una lista de partidas mockeada
        List<Partida> partidasMock = List.of(new Partida(), new Partida(), new Partida());

        when(lobbyServiceMock.obtenerPartidasEnEspera()).thenReturn(partidasMock);

        assertNotNull(partidasMock);
        assertEquals(3, partidasMock.size());
    }

}
