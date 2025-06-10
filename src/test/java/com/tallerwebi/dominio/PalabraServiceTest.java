package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PalabraServiceTest {

    @Mock
    private PalabraRepository palabraRepository;

    @InjectMocks
    private PalabraServicioImpl rondaServicio;

    private List<Palabra> palabrasMock;
    private Palabra palabraMock;
    private List<Definicion> definicionesMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Configurar definiciones mock
        definicionesMock = Arrays.asList(
                crearDefinicion("Definición 1"),
                crearDefinicion("Definición 2"),
                crearDefinicion("Definición 3")
        );

        // Configurar palabra mock
        palabraMock = crearPalabra("Casa", "es", definicionesMock);

        // Lista de palabras mock
        palabrasMock = Arrays.asList(palabraMock);
    }

    @Test
    void traerPalabraYDefinicion_ConIdiomaEnglish_DeberiaLlamarBuscarPorIdioma() {
        // Given
        when(palabraRepository.buscarPorIdioma("en")).thenReturn(palabrasMock);

        // When
        HashMap<Palabra, List<Definicion>> resultado = rondaServicio.traerPalabraYDefinicion("English");

        // Then
        verify(palabraRepository).buscarPorIdioma("en");
        verify(palabraRepository, never()).buscarTodas();
        verify(palabraRepository, never()).buscarPorIdioma("es");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Corregido: usar el objeto Palabra como clave, no el String
        assertTrue(resultado.containsKey(palabraMock));
        assertEquals(definicionesMock, resultado.get(palabraMock));
    }

    @Test
    void traerPalabraYDefinicion_ConIdiomaCastellano_DeberiaLlamarBuscarPorIdiomaEs() {
        // Given
        when(palabraRepository.buscarPorIdioma("es")).thenReturn(palabrasMock);

        // When
        HashMap<Palabra, List<Definicion>> resultado = rondaServicio.traerPalabraYDefinicion("Castellano");

        // Then
        verify(palabraRepository).buscarPorIdioma("es");
        verify(palabraRepository, never()).buscarTodas();
        verify(palabraRepository, never()).buscarPorIdioma("en");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Corregido: usar el objeto Palabra como clave
        assertTrue(resultado.containsKey(palabraMock));
        assertEquals(definicionesMock, resultado.get(palabraMock));
    }

    @Test
    void traerPalabraYDefinicion_ConIdiomaMixto_DeberiaLlamarBuscarTodas() {
        // Given
        when(palabraRepository.buscarTodas()).thenReturn(palabrasMock);

        // When
        HashMap<Palabra, List<Definicion>> resultado = rondaServicio.traerPalabraYDefinicion("Mixto");

        // Then
        verify(palabraRepository).buscarTodas();
        verify(palabraRepository, never()).buscarPorIdioma(anyString());

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Corregido: usar el objeto Palabra como clave
        assertTrue(resultado.containsKey(palabraMock));
        assertEquals(definicionesMock, resultado.get(palabraMock));
    }

    @Test
    void traerPalabraYDefinicion_ConIdiomaDesconocido_DeberiaUsarIngles() {
        // Given
        when(palabraRepository.buscarPorIdioma("en")).thenReturn(palabrasMock);

        // When
        HashMap<Palabra, List<Definicion>> resultado = rondaServicio.traerPalabraYDefinicion("Frances");

        // Then
        verify(palabraRepository).buscarPorIdioma("en");
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Corregido: usar el objeto Palabra como clave
        assertTrue(resultado.containsKey(palabraMock));
    }

    @Test
    void traerPalabraYDefinicion_ConListaVacia_DeberiaLanzarRuntimeException() {
        // Given
        when(palabraRepository.buscarTodas()).thenReturn(Collections.emptyList());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rondaServicio.traerPalabraYDefinicion("Mixto");
        });

        assertEquals("No hay palabras disponibles", exception.getMessage());
    }

    @Test
    void traerPalabraYDefinicion_ConVariasPalabras_DeberiaSeleccionarUnaAleatoriamente() {
        // Given
        Palabra palabra1 = crearPalabra("Casa", "es", definicionesMock);
        Palabra palabra2 = crearPalabra("Perro", "es", definicionesMock);
        Palabra palabra3 = crearPalabra("Gato", "es", definicionesMock);

        List<Palabra> variasPalabras = Arrays.asList(palabra1, palabra2, palabra3);
        when(palabraRepository.buscarTodas()).thenReturn(variasPalabras);

        // When
        HashMap<Palabra, List<Definicion>> resultado = rondaServicio.traerPalabraYDefinicion("Mixto");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Verificar que la palabra seleccionada está en la lista original
        Palabra palabraSeleccionada = resultado.keySet().iterator().next();
        assertTrue(variasPalabras.contains(palabraSeleccionada));

        // Verificar que la descripción es una de las esperadas
        List<String> descripciones = Arrays.asList("Casa", "Perro", "Gato");
        assertTrue(descripciones.contains(palabraSeleccionada.getDescripcion()));
    }

    @Test
    void traerPalabraYDefinicion_DeberiaRetornarHashMapConFormatoCorrect() {
        // Given
        when(palabraRepository.buscarTodas()).thenReturn(palabrasMock);

        // When
        HashMap<Palabra, List<Definicion>> resultado = rondaServicio.traerPalabraYDefinicion("Mixto");

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());

        // Verificar que cada entrada tiene una clave (Palabra) y valor (lista de definiciones)
        for (Map.Entry<Palabra, List<Definicion>> entry : resultado.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            assertFalse(entry.getValue().isEmpty());

            // Verificar que la clave es una instancia de Palabra
            assertTrue(entry.getKey() instanceof Palabra);
        }
    }

    // Métodos auxiliares para crear objetos mock
    private Palabra crearPalabra(String descripcion, String idioma, List<Definicion> definiciones) {
        Palabra palabra = new Palabra();
        palabra.setDescripcion(descripcion);
        palabra.setIdioma(idioma);
        palabra.setDefinicion(definiciones);
        return palabra;
    }

    private Definicion crearDefinicion(String texto) {
        Definicion definicion = new Definicion();
        definicion.setDefinicion(texto);
        return definicion;
    }
}