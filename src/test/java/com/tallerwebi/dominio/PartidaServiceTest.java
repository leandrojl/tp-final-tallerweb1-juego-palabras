package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class PartidaServiceTest {

    private PartidaService partidaServiceMock;

    @BeforeEach
    void setUp() {
        partidaServiceMock = mock(PartidaService.class);
    }

    @Test
    public void testObtenerPartidasDisponiblesConMock() {

        when(partidaServiceMock.obtenerPartidasDisponibles()).thenReturn(List.of(
                new Partida("Partida de prueba"),
                new Partida("Partida de prueba 2"),
                new Partida("Partida de prueba 3")
        ));

        List<Partida> partidas = partidaServiceMock.obtenerPartidasDisponibles();

        Assertions.assertEquals(3, partidas.size());
        Assertions.assertEquals("Partida de prueba", partidas.get(0).getNombrePartida());
        Assertions.assertEquals("Partida de prueba 2", partidas.get(1).getNombrePartida());
        Assertions.assertEquals("Partida de prueba 3", partidas.get(2).getNombrePartida());

        verify(partidaServiceMock, times(1)).obtenerPartidasDisponibles();
    }

}
