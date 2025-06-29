package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioPartidaServiceTest {

    private PartidaService partidaServiceMock;
    private UsuarioPartidaService usuarioPartidaServiceMock;

    @BeforeEach
    void setUp() {
        partidaServiceMock = mock(PartidaService.class);
        usuarioPartidaServiceMock = mock(UsuarioPartidaService.class);
    }

    @Test
    void deberiaAgregarUsuarioAPartidaConDatosValidos() {
        Long usuarioId = 1L;
        Long partidaId = 2L;
        int puntaje = 0;
        boolean gano = false;
        Estado estado = Estado.EN_ESPERA;

        usuarioPartidaServiceMock.agregarUsuarioAPartida(usuarioId, partidaId, puntaje, gano, estado);

        verify(usuarioPartidaServiceMock, times(1))
                .agregarUsuarioAPartida(usuarioId, partidaId, puntaje, gano, estado);
    }

    @Test
    void deberiaLanzarExcepcionSiUsuarioIdEsNull() {
        Long usuarioId = null;
        Long partidaId = 2L;
        int puntaje = 0;
        boolean gano = false;
        Estado estado = Estado.EN_ESPERA;

        doThrow(new IllegalArgumentException("usuarioId no puede ser null"))
                .when(usuarioPartidaServiceMock)
                .agregarUsuarioAPartida(isNull(), eq(partidaId), anyInt(), anyBoolean(), any(Estado.class));

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioPartidaServiceMock.agregarUsuarioAPartida(usuarioId, partidaId, puntaje, gano, estado);
        });
    }
}