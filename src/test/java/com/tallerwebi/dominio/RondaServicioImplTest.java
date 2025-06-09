package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RondaServicioImplTest {

    @Mock
    private RondaRepository rondaRepository;

    @Mock
    private PalabraService palabraService;

    private RondaServicioImpl rondaServicio;

    private Partida2 partida;
    private HashMap<String, List<Definicion>> palabraYDefiniciones;
    private List<Definicion> definiciones;
    private Ronda rondaEsperada;

    @BeforeEach
    void setUp() {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this);

        // Crear la instancia manualmente ya que no usamos @InjectMocks
        rondaServicio = new RondaServicioImpl(rondaRepository, palabraService);

        // Configurar partida mock
        partida = new Partida2();
        partida.setId(1L);

        // Configurar definiciones
        definiciones = new ArrayList<>();
        Definicion def1 = new Definicion();
        def1.setDefinicion("Primera definición");
        Definicion def2 = new Definicion();
        def2.setDefinicion("Segunda definición");
        definiciones.add(def1);
        definiciones.add(def2);

        // Configurar HashMap de palabra y definiciones
        palabraYDefiniciones = new HashMap<>();
        palabraYDefiniciones.put("casa", definiciones);

        // Configurar ronda esperada
        rondaEsperada = new Ronda();
        rondaEsperada.setId(1L);
        rondaEsperada.setPartida(partida);
        rondaEsperada.setEstado(Estado.EN_CURSO);
    }

    @Test
    void deberia_crear_nueva_ronda_exitosamente_con_idioma_castellano() {
        // Given
        String idioma = "Castellano";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);

        // Mock que devuelve la misma ronda que recibe (simulando persistencia)
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> {
            Ronda ronda = invocation.getArgument(0);
            ronda.setId(1L); // Simular que se asignó un ID
            return ronda;
        });

        // When
        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        // Then
        assertNotNull(resultado);
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
        assertEquals(partida, resultado.getPartida());
        assertNotNull(resultado.getFechaHora());
        assertNotNull(resultado.getId());

        // Verificar que se llamaron los métodos correctos
        verify(palabraService, times(1)).traerPalabraYDefinicion(idioma);
        verify(rondaRepository, times(1)).guardar(any(Ronda.class));
    }

    @Test
    void deberia_crear_nueva_ronda_exitosamente_con_idioma_ingles() {
        // Given
        String idioma = "English";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);

        // Mock que devuelve la misma ronda que recibe (simulando persistencia)
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> {
            Ronda ronda = invocation.getArgument(0);
            ronda.setId(1L); // Simular que se asignó un ID
            return ronda;
        });

        // When
        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        // Then
        assertNotNull(resultado);
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
        assertEquals(partida, resultado.getPartida());
        assertNotNull(resultado.getFechaHora());
        assertNotNull(resultado.getId());

        verify(palabraService, times(1)).traerPalabraYDefinicion(idioma);
        verify(rondaRepository, times(1)).guardar(any(Ronda.class));
    }

    @Test
    void deberia_configurar_palabra_correctamente_con_idioma_castellano() {
        // Given
        String idioma = "Castellano";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);

        // Capturar la ronda que se guarda para verificar la palabra
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> {
            Ronda ronda = invocation.getArgument(0);
            ronda.setId(1L);
            return ronda;
        });

        // When
        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        // Then
        Palabra palabra = resultado.getPalabra();
        assertNotNull(palabra);
        assertEquals("casa", palabra.getDescripcion());
        assertEquals("es", palabra.getIdioma());
        assertEquals(definiciones, palabra.getDefinicion());
    }

    @Test
    void deberia_configurar_palabra_correctamente_con_idioma_ingles() {
        // Given
        String idioma = "English";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);

        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> {
            Ronda ronda = invocation.getArgument(0);
            ronda.setId(1L);
            return ronda;
        });

        // When
        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        // Then
        Palabra palabra = resultado.getPalabra();
        assertNotNull(palabra);
        assertEquals("casa", palabra.getDescripcion());
        assertEquals("en", palabra.getIdioma());
        assertEquals(definiciones, palabra.getDefinicion());
    }

    @Test
    void deberia_lanzar_excepcion_cuando_palabraYDef_es_null() {
        // Given
        String idioma = "Castellano";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(null);

        // When & Then
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> rondaServicio.crearNuevaRonda(partida, idioma)
        );

        assertEquals("No se encontraron palabras para crear la ronda", excepcion.getMessage());
        verify(rondaRepository, never()).guardar(any(Ronda.class));
    }

    @Test
    void deberia_lanzar_excepcion_cuando_palabraYDef_esta_vacio() {
        // Given
        String idioma = "Castellano";
        HashMap<String, List<Definicion>> hashMapVacio = new HashMap<>();
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(hashMapVacio);

        // When & Then
        IllegalArgumentException excepcion = assertThrows(
                IllegalArgumentException.class,
                () -> rondaServicio.crearNuevaRonda(partida, idioma)
        );

        assertEquals("No se encontraron palabras para crear la ronda", excepcion.getMessage());
        verify(rondaRepository, never()).guardar(any(Ronda.class));
    }

    @Test
    void deberia_asignar_fecha_y_hora_actual_a_la_ronda() {
        // Given
        String idioma = "Castellano";
        LocalDateTime antesDeEjecucion = LocalDateTime.now();
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);

        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> {
            Ronda ronda = invocation.getArgument(0);
            ronda.setId(1L);
            return ronda;
        });

        // When
        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);
        LocalDateTime despuesDeEjecucion = LocalDateTime.now();

        // Then
        assertNotNull(resultado.getFechaHora());
        assertTrue(resultado.getFechaHora().isAfter(antesDeEjecucion) ||
                resultado.getFechaHora().isEqual(antesDeEjecucion));
        assertTrue(resultado.getFechaHora().isBefore(despuesDeEjecucion) ||
                resultado.getFechaHora().isEqual(despuesDeEjecucion));
    }

    @Test
    void deberia_manejar_correctamente_multiples_palabras_en_hashmap() {
        // Given
        String idioma = "Castellano";
        HashMap<String, List<Definicion>> multiplesPalabras = new HashMap<>();
        multiplesPalabras.put("casa", definiciones);
        multiplesPalabras.put("perro", new ArrayList<>());

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(multiplesPalabras);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> {
            Ronda ronda = invocation.getArgument(0);
            ronda.setId(1L);
            return ronda;
        });

        // When
        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getPalabra());
        // Debería tomar una de las palabras disponibles
        assertTrue(multiplesPalabras.containsKey(resultado.getPalabra().getDescripcion()));
    }

    @Test
    void deberia_verificar_que_ronda_tiene_todos_los_campos_requeridos() {
        // Given
        String idioma = "Castellano";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> {
            Ronda ronda = invocation.getArgument(0);
            ronda.setId(1L);
            return ronda;
        });

        // When
        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        // Then - Verificar todos los campos obligatorios
        assertNotNull(resultado.getPartida(), "La partida no debe ser null");
        assertNotNull(resultado.getPalabra(), "La palabra no debe ser null");
        assertNotNull(resultado.getEstado(), "El estado no debe ser null");
        assertNotNull(resultado.getFechaHora(), "La fecha y hora no debe ser null");
        assertEquals(Estado.EN_CURSO, resultado.getEstado(), "El estado debe ser EN_CURSO");
        assertEquals(partida, resultado.getPartida(), "La partida debe coincidir");
    }
}