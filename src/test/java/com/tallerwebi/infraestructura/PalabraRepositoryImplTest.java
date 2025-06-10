package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.PalabraRepositoryImpl;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(HibernateTestConfig.class) // Combina @ExtendWith(SpringExtension.class) y @ContextConfiguration
@Transactional
@Rollback
public class PalabraRepositoryImplTest {

    @Autowired
    private SessionFactory sessionFactory;

    private PalabraRepositoryImpl palabraRepository;

    @BeforeEach
    void setUp() {
        palabraRepository = new PalabraRepositoryImpl(sessionFactory);
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("DELETE FROM Definicion").executeUpdate(); // Primero definiciones por FK
        session.createQuery("DELETE FROM Palabra").executeUpdate();
    }

    @Test
    void guardar_DeberiaGuardarPalabraEnBaseDeDatos() {
        Palabra palabra = crearPalabra("Casa", "es");

        palabraRepository.guardar(palabra);

        List<Palabra> palabras = palabraRepository.buscarTodas();
        assertFalse(palabras.isEmpty());
        assertEquals("Casa", palabras.get(0).getDescripcion());
        assertEquals("es", palabras.get(0).getIdioma());
    }

    @Test
    void buscarPorIdioma_ConIdiomaEspanol_DeberiaRetornarSoloPalabrasEnEspanol() {
        palabraRepository.guardar(crearPalabra("Casa", "es"));
        palabraRepository.guardar(crearPalabra("House", "en"));

        List<Palabra> resultado = palabraRepository.buscarPorIdioma("es");

        assertEquals(1, resultado.size());
        assertEquals("Casa", resultado.get(0).getDescripcion());
        assertEquals("es", resultado.get(0).getIdioma());
    }

    @Test
    void buscarPorIdioma_ConIdiomaIngles_DeberiaRetornarSoloPalabrasEnIngles() {
        palabraRepository.guardar(crearPalabra("Casa", "es"));
        palabraRepository.guardar(crearPalabra("House", "en"));

        List<Palabra> resultado = palabraRepository.buscarPorIdioma("en");

        assertEquals(1, resultado.size());
        assertEquals("House", resultado.get(0).getDescripcion());
        assertEquals("en", resultado.get(0).getIdioma());
    }

    @Test
    void buscarPorIdioma_ConIdiomaInexistente_DeberiaRetornarListaVacia() {
        palabraRepository.guardar(crearPalabra("Casa", "es"));

        List<Palabra> resultado = palabraRepository.buscarPorIdioma("fr");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void buscarTodas_DeberiaRetornarTodasLasPalabras() {
        palabraRepository.guardar(crearPalabra("Casa", "es"));
        palabraRepository.guardar(crearPalabra("House", "en"));
        palabraRepository.guardar(crearPalabra("Perro", "es"));

        List<Palabra> resultado = palabraRepository.buscarTodas();

        assertEquals(3, resultado.size());

        List<String> descripciones = Arrays.asList("Casa", "House", "Perro");
        for (Palabra palabra : resultado) {
            assertTrue(descripciones.contains(palabra.getDescripcion()));
        }
    }

    @Test
    void buscarTodas_SinPalabras_DeberiaRetornarListaVacia() {
        List<Palabra> resultado = palabraRepository.buscarTodas();
        assertTrue(resultado.isEmpty());
    }

    @Test
    void contar_ConVariasPalabras_DeberiaRetornarCantidadCorrecta() {
        palabraRepository.guardar(crearPalabra("Casa", "es"));
        palabraRepository.guardar(crearPalabra("House", "en"));
        palabraRepository.guardar(crearPalabra("Perro", "es"));

        Long cantidad = palabraRepository.contar();

        assertEquals(3L, cantidad);
    }

    @Test
    void contar_SinPalabras_DeberiaRetornarCero() {
        Long cantidad = palabraRepository.contar();
        assertEquals(0L, cantidad);
    }

    @Test
    void buscarPorIdioma_ConVariasPalabrasDelMismoIdioma_DeberiaRetornarTodas() {
        palabraRepository.guardar(crearPalabra("Casa", "es"));
        palabraRepository.guardar(crearPalabra("Perro", "es"));
        palabraRepository.guardar(crearPalabra("Gato", "es"));
        palabraRepository.guardar(crearPalabra("House", "en"));

        List<Palabra> resultado = palabraRepository.buscarPorIdioma("es");

        assertEquals(3, resultado.size());
        for (Palabra palabra : resultado) {
            assertEquals("es", palabra.getIdioma());
        }
    }

    @Test
    void guardar_PalabraConDefiniciones_DeberiaGuardarCorrectamente() {
        Palabra palabra = crearPalabra("Casa", "es");
        Definicion def1 = crearDefinicion("Lugar donde vive una familia");
        Definicion def2 = crearDefinicion("Edificio para habitar");
        palabra.setDefinicion(Arrays.asList(def1, def2));

        palabraRepository.guardar(palabra);

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
