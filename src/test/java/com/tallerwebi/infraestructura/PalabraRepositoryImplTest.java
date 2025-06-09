package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.PalabraRepositoryImpl;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(HibernateTestConfig.class)
@Transactional
public class PalabraRepositoryImplTest {

    @Autowired
    private SessionFactory sessionFactory;

    private PalabraRepositoryImpl palabraRepository;

    @BeforeEach
    void setUp() {
        palabraRepository = new PalabraRepositoryImpl(sessionFactory);
    }

    @Test
    void guardar_DeberiaGuardarPalabraEnBaseDeDatos() {
        // Given
        Palabra palabra = crearPalabra("Casa", "es");

        // When
        palabraRepository.guardar(palabra);

        // Then
        List<Palabra> palabras = palabraRepository.buscarTodas();
        assertFalse(palabras.isEmpty());
        assertEquals("Casa", palabras.get(0).getDescripcion());
        assertEquals("es", palabras.get(0).getIdioma());
    }

    @Test
    void buscarPorIdioma_ConIdiomaEspanol_DeberiaRetornarSoloPalabrasEnEspanol() {
        // Given
        Palabra palabraEs = crearPalabra("Casa", "es");
        Palabra palabraEn = crearPalabra("House", "en");

        palabraRepository.guardar(palabraEs);
        palabraRepository.guardar(palabraEn);

        // When
        List<Palabra> resultado = palabraRepository.buscarPorIdioma("es");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("Casa", resultado.get(0).getDescripcion());
        assertEquals("es", resultado.get(0).getIdioma());
    }

    @Test
    void buscarPorIdioma_ConIdiomaIngles_DeberiaRetornarSoloPalabrasEnIngles() {
        // Given
        Palabra palabraEs = crearPalabra("Casa", "es");
        Palabra palabraEn = crearPalabra("House", "en");

        palabraRepository.guardar(palabraEs);
        palabraRepository.guardar(palabraEn);

        // When
        List<Palabra> resultado = palabraRepository.buscarPorIdioma("en");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("House", resultado.get(0).getDescripcion());
        assertEquals("en", resultado.get(0).getIdioma());
    }

    @Test
    void buscarPorIdioma_ConIdiomaInexistente_DeberiaRetornarListaVacia() {
        // Given
        Palabra palabra = crearPalabra("Casa", "es");
        palabraRepository.guardar(palabra);

        // When
        List<Palabra> resultado = palabraRepository.buscarPorIdioma("fr");

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarTodas_DeberiaRetornarTodasLasPalabras() {
        // Given
        Palabra palabra1 = crearPalabra("Casa", "es");
        Palabra palabra2 = crearPalabra("House", "en");
        Palabra palabra3 = crearPalabra("Perro", "es");

        palabraRepository.guardar(palabra1);
        palabraRepository.guardar(palabra2);
        palabraRepository.guardar(palabra3);

        // When
        List<Palabra> resultado = palabraRepository.buscarTodas();

        // Then
        assertEquals(3, resultado.size());

        // Verificar que están todas las palabras
        List<String> descripciones = Arrays.asList("Casa", "House", "Perro");
        for (Palabra palabra : resultado) {
            assertTrue(descripciones.contains(palabra.getDescripcion()));
        }
    }

    @Test
    void buscarTodas_SinPalabras_DeberiaRetornarListaVacia() {
        // When
        List<Palabra> resultado = palabraRepository.buscarTodas();

        // Then
        assertTrue(resultado.isEmpty());
    }

    @Test
    void contar_ConVariasPalabras_DeberiaRetornarCantidadCorrecta() {
        // Given
        palabraRepository.guardar(crearPalabra("Casa", "es"));
        palabraRepository.guardar(crearPalabra("House", "en"));
        palabraRepository.guardar(crearPalabra("Perro", "es"));

        // When
        Long cantidad = palabraRepository.contar();

        // Then
        assertEquals(Long.valueOf(3), cantidad);
    }

    @Test
    void contar_SinPalabras_DeberiaRetornarCero() {
        // When
        Long cantidad = palabraRepository.contar();

        // Then
        assertEquals(Long.valueOf(0), cantidad);
    }

    @Test
    void buscarPorIdioma_ConVariasPalabrasDelMismoIdioma_DeberiaRetornarTodas() {
        // Given
        palabraRepository.guardar(crearPalabra("Casa", "es"));
        palabraRepository.guardar(crearPalabra("Perro", "es"));
        palabraRepository.guardar(crearPalabra("Gato", "es"));
        palabraRepository.guardar(crearPalabra("House", "en"));

        // When
        List<Palabra> resultado = palabraRepository.buscarPorIdioma("es");

        // Then
        assertEquals(3, resultado.size());
        for (Palabra palabra : resultado) {
            assertEquals("es", palabra.getIdioma());
        }
    }

    @Test
    void guardar_PalabraConDefiniciones_DeberiaGuardarCorrectamente() {
        // Given
        Palabra palabra = crearPalabra("Casa", "es");
        Definicion def1 = crearDefinicion("Lugar donde vive una familia");
        Definicion def2 = crearDefinicion("Edificio para habitar");
        palabra.setDefinicion(Arrays.asList(def1, def2));

        // When
        palabraRepository.guardar(palabra);

        // Then
        List<Palabra> palabras = palabraRepository.buscarTodas();
        assertEquals(1, palabras.size());

        Palabra palabraGuardada = palabras.get(0);
        assertEquals("Casa", palabraGuardada.getDescripcion());
        assertNotNull(palabraGuardada.getDefinicion());
        assertEquals(2, palabraGuardada.getDefinicion().size());
    }

    // Métodos auxiliares
    private Palabra crearPalabra(String descripcion, String idioma) {
        Palabra palabra = new Palabra();
        palabra.setDescripcion(descripcion);
        palabra.setIdioma(idioma);
        return palabra;
    }

    private Definicion crearDefinicion(String texto) {
        Definicion definicion = new Definicion();
        definicion.setDefinicion(texto);
        return definicion;
    }
}