package com.tallerwebi.dominio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.RondaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
public class RondaServiceTest {

    @InjectMocks
    private RondaServiceImpl rondaServicio;

    @Mock
    private RondaRepository rondaRepositorio;

    @Mock
    private PartidaRepository partidaRepositorio;

    @Mock
    private PalabraServicio palabraServicio;

    @Test
    void queRondaMeTraigaUnaDefinicion() {
        // Arrange
        Long partidaId = 1L;
        String idioma = "Castellano";

        Partida2 partidaMock = new Partida2();
        partidaMock.setId(partidaId);

        Palabra palabraMock = new Palabra();
        palabraMock.setDescripcion("Casa");

        when(partidaRepositorio.buscarPorId(partidaId)).thenReturn(partidaMock);
        when(rondaRepositorio.obtenerCantidadDeRondasPorPartida(partidaId)).thenReturn(0); // para que sea ronda 1
        when(palabraServicio.obtenerPalabraConDefinicionesDesdeHelper(idioma)).thenReturn(palabraMock);

        // Act
        Ronda nuevaRonda = rondaServicio.crearRonda(partidaId, idioma);

        // Assert
        assertNotNull(nuevaRonda);
        assertEquals(1, nuevaRonda.getNumeroDeRonda());
        assertEquals(partidaMock, nuevaRonda.getPartida());
        assertEquals(palabraMock, nuevaRonda.getPalabra());
        assertEquals(Estado.EN_CURSO, nuevaRonda.getEstado());
        assertNotNull(nuevaRonda.getFechaHora());

        verify(rondaRepositorio).guardar(any(Ronda.class)); // asegurarse que se haya guardado
    }
}

