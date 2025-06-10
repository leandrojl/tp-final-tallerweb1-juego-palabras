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
import java.util.*;
import java.util.stream.Collectors;

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
    private HashMap<Palabra, List<Definicion>> palabraYDefiniciones;
    private List<Definicion> definiciones;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rondaServicio = new RondaServicioImpl(rondaRepository, palabraService);

        partida = new Partida2();
        partida.setId(1L);

        definiciones = new ArrayList<>();
        Definicion def1 = new Definicion();
        def1.setDefinicion("Primera definición");
        Definicion def2 = new Definicion();
        def2.setDefinicion("Segunda definición");
        definiciones.add(def1);
        definiciones.add(def2);

        palabraYDefiniciones = new HashMap<>();
        Palabra palabra = new Palabra();
        palabra.setDescripcion("casa");
        palabraYDefiniciones.put(palabra, definiciones);
    }

    @Test
    void deberia_crear_nueva_ronda_exitosamente_con_idioma_castellano() {
        String idioma = "Castellano";

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        assertNotNull(resultado);
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
        assertEquals(partida, resultado.getPartida());
        assertNotNull(resultado.getFechaHora());

        verify(palabraService, times(1)).traerPalabraYDefinicion(idioma);
        verify(rondaRepository, times(1)).guardar(any(Ronda.class));
    }

    @Test
    void deberia_crear_nueva_ronda_exitosamente_con_idioma_ingles() {
        String idioma = "English";

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        assertNotNull(resultado);
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
        assertEquals(partida, resultado.getPartida());
        assertNotNull(resultado.getFechaHora());

        verify(palabraService, times(1)).traerPalabraYDefinicion(idioma);
        verify(rondaRepository, times(1)).guardar(any(Ronda.class));
    }

    @Test
    void deberia_configurar_palabra_correctamente_con_idioma_castellano() {
        String idioma = "Castellano";

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        Palabra palabra = resultado.getPalabra();
        assertNotNull(palabra);
        assertEquals("casa", palabra.getDescripcion());
        assertEquals("es", palabra.getIdioma());
        assertEquals(definiciones, palabra.getDefinicion());
    }

    @Test
    void deberia_configurar_palabra_correctamente_con_idioma_ingles() {
        String idioma = "English";

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        Palabra palabra = resultado.getPalabra();
        assertNotNull(palabra);
        assertEquals("casa", palabra.getDescripcion());
        assertEquals("en", palabra.getIdioma());
        assertEquals(definiciones, palabra.getDefinicion());
    }

    @Test
    void deberia_lanzar_excepcion_cuando_palabraYDef_es_null() {
        String idioma = "Castellano";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            rondaServicio.crearNuevaRonda(partida, idioma);
        });

        assertEquals("No se encontraron palabras para crear la ronda", thrown.getMessage());
        verify(rondaRepository, never()).guardar(any(Ronda.class));
    }

    @Test
    void deberia_lanzar_excepcion_cuando_palabraYDef_esta_vacio() {
        String idioma = "Castellano";
        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(new HashMap<>());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            rondaServicio.crearNuevaRonda(partida, idioma);
        });

        assertEquals("No se encontraron palabras para crear la ronda", thrown.getMessage());
        verify(rondaRepository, never()).guardar(any(Ronda.class));
    }

    @Test
    void deberia_asignar_fecha_y_hora_actual_a_la_ronda() {
        String idioma = "Castellano";
        LocalDateTime antes = LocalDateTime.now();

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);
        LocalDateTime despues = LocalDateTime.now();

        assertNotNull(resultado.getFechaHora());
        assertTrue(!resultado.getFechaHora().isBefore(antes));
        assertTrue(!resultado.getFechaHora().isAfter(despues));
    }

    @Test
    void deberia_manejar_correctamente_multiples_palabras_en_hashmap() {
        String idioma = "Castellano";
        HashMap<Palabra, List<Definicion>> multiples = new HashMap<>();

        Palabra p1 = new Palabra();
        p1.setDescripcion("casa");
        Palabra p2 = new Palabra();
        p2.setDescripcion("perro");

        multiples.put(p1, definiciones);
        multiples.put(p2, new ArrayList<>());

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(multiples);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        assertNotNull(resultado);
        assertNotNull(resultado.getPalabra());

        Set<String> descripciones = multiples.keySet().stream()
                .map(Palabra::getDescripcion)
                .collect(Collectors.toSet());

        assertTrue(descripciones.contains(resultado.getPalabra().getDescripcion()));
    }

    @Test
    void deberia_verificar_que_ronda_tiene_todos_los_campos_requeridos() {
        String idioma = "Castellano";

        when(palabraService.traerPalabraYDefinicion(idioma)).thenReturn(palabraYDefiniciones);
        when(rondaRepository.guardar(any(Ronda.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ronda resultado = rondaServicio.crearNuevaRonda(partida, idioma);

        assertNotNull(resultado.getPartida(), "La partida no debe ser null");
        assertNotNull(resultado.getPalabra(), "La palabra no debe ser null");
        assertNotNull(resultado.getEstado(), "El estado no debe ser null");
        assertNotNull(resultado.getFechaHora(), "La fecha y hora no debe ser null");
        assertEquals(Estado.EN_CURSO, resultado.getEstado(), "El estado debe ser EN_CURSO");
        assertEquals(partida, resultado.getPartida(), "La partida debe coincidir");
    }
}
