package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.LobbyRepository;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.infraestructura.LobbyRepositoryImpl;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//estas anotaciones deben ir en la clase de test de los repositorios
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class LobbyRepositoryTest {

    @Autowired
    private LobbyRepository lobbyRepository;

    @BeforeEach
    public void setUp() {
        lobbyRepository.eliminarTodasLasPartidas();
    }

    @Test
    public void testObtenerPartidasEnEspera() {
        // Verificar que el repositorio esté vacío
        List<Partida2> partidasIniciales = lobbyRepository.obtenerPartidasEnEspera();
        assertEquals(0, partidasIniciales.size());

        // Crear y guardar partidas en espera en la base de datos
        Partida2 partida1 = new Partida2("Partida en espera 1", "Español", true, 5, 2, Estado.EN_ESPERA);
        Partida2 partida2 = new Partida2("Partida en espera 2", "Inglés", false, 3, 4, Estado.EN_ESPERA);
        Partida2 partida3 = new Partida2("Partida en espera 3", "Francés", true, 7, 3, Estado.EN_ESPERA);

        lobbyRepository.guardar(partida1);
        lobbyRepository.guardar(partida2);
        lobbyRepository.guardar(partida3);

        // Obtener las partidas en espera desde el repositorio
        List<Partida2> partidasEnEspera = lobbyRepository.obtenerPartidasEnEspera();

        // Verificar que las partidas en espera se obtienen correctamente
        assertNotNull(partidasEnEspera);
        assertEquals(3, partidasEnEspera.size());
        assertEquals(Estado.EN_ESPERA, partidasEnEspera.get(0).getEstado());
        assertEquals(Estado.EN_ESPERA, partidasEnEspera.get(1).getEstado());
        assertEquals(Estado.EN_ESPERA, partidasEnEspera.get(2).getEstado());
    }

    @Test
    public void testGuardarPartida() {

        Partida2 nuevaPartida = new Partida2("Partida nueva", "Alemán", false, 4, 1, Estado.EN_ESPERA);

        lobbyRepository.guardar(nuevaPartida);

        List<Partida2> partidasEnEspera = lobbyRepository.obtenerPartidasEnEspera();

        assertNotNull(partidasEnEspera);
        assertEquals(1, partidasEnEspera.size());
        assertTrue(partidasEnEspera.contains(nuevaPartida));
    }

    @Test
    public void testEliminarTodasLasPartidas() {
        // Crear y guardar partidas en la base de datos
        Partida2 partida1 = new Partida2("Partida 1", "Español", true, 5, 2, Estado.EN_ESPERA);
        Partida2 partida2 = new Partida2("Partida 2", "Inglés", false, 3, 4, Estado.EN_ESPERA);

        lobbyRepository.guardar(partida1);
        lobbyRepository.guardar(partida2);

        // Verificar que las partidas fueron guardadas
        List<Partida2> partidasAntesDeEliminar = lobbyRepository.obtenerPartidasEnEspera();
        assertEquals(2, partidasAntesDeEliminar.size());

        // Eliminar todas las partidas
        lobbyRepository.eliminarTodasLasPartidas();

        // Verificar que no quedan partidas en el repositorio
        List<Partida2> partidasDespuesDeEliminar = lobbyRepository.obtenerPartidasEnEspera();
        assertEquals(0, partidasDespuesDeEliminar.size());
    }
}