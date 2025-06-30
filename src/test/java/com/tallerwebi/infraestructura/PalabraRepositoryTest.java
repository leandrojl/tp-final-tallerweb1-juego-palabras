package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SimpMessagingMockConfigTest;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class, SimpMessagingMockConfigTest.class})
public class PalabraRepositoryTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private PalabraRepositoryImpl palabraRepository;

    @Test
    @Transactional
    @Rollback
    public void queSePuedaGuardarYBuscarPalabraPorTexto() {
        // Arrange
        Palabra palabra = new Palabra();
        palabra.setDescripcion("casa");
        palabra.setIdioma("Castellano");

        // Act
        palabraRepository.guardar(palabra);

        // Assert
        Palabra palabraEncontrada = palabraRepository.buscarPorTexto("casa");
        assertNotNull(palabraEncontrada);
        assertEquals("casa", palabraEncontrada.getDescripcion());
        assertEquals("Castellano", palabraEncontrada.getIdioma());
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedanBuscarPalabrasPorIdioma() {
        // Arrange
        Palabra palabra1 = new Palabra();
        palabra1.setDescripcion("río");
        palabra1.setIdioma("Castellano");

        Palabra palabra2 = new Palabra();
        palabra2.setDescripcion("tree");
        palabra2.setIdioma("Ingles");

        sessionFactory.getCurrentSession().save(palabra1);
        sessionFactory.getCurrentSession().save(palabra2);

        // Act
        List<Palabra> castellanas = palabraRepository.buscarPorIdioma("Castellano");

        // Assert
        assertFalse(castellanas.isEmpty());
        assertTrue(castellanas.stream().anyMatch(p -> p.getDescripcion().equals("río")));
        assertTrue(castellanas.stream().noneMatch(p -> p.getDescripcion().equals("tree")));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedanBuscarTodasLasPalabras() {
        // Arrange
        Palabra palabra1 = new Palabra();
        palabra1.setDescripcion("nube");
        palabra1.setIdioma("Castellano");

        Palabra palabra2 = new Palabra();
        palabra2.setDescripcion("sky");
        palabra2.setIdioma("Ingles");

        sessionFactory.getCurrentSession().save(palabra1);
        sessionFactory.getCurrentSession().save(palabra2);

        // Act
        List<Palabra> todas = palabraRepository.buscarTodas();

        // Assert
        assertTrue(todas.size() >= 2);
        assertTrue(todas.stream().anyMatch(p -> p.getDescripcion().equals("nube")));
        assertTrue(todas.stream().anyMatch(p -> p.getDescripcion().equals("sky")));
    }
}


