package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.RondaRepositoryImpl;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(HibernateTestConfig.class)
@Transactional
class RondaRepositoryImplTest {

    @Autowired
    private SessionFactory sessionFactory;

    private RondaRepositoryImpl rondaRepository;
    private Partida2 partida1;
    private Partida2 partida2;
    private Ronda ronda1;
    private Ronda ronda2;
    private Ronda ronda3;
    private Palabra palabra;

    @BeforeEach
    void setUp() {
        rondaRepository = new RondaRepositoryImpl(sessionFactory);

        // Crear palabra de prueba
        palabra = new Palabra();
        palabra.setDescripcion("casa");
        palabra.setIdioma("es");
        sessionFactory.getCurrentSession().save(palabra);

        // Crear partidas de prueba
        partida1 = new Partida2();
        partida1.setNombre("Partida Test 1");
        sessionFactory.getCurrentSession().save(partida1);

        partida2 = new Partida2();
        partida2.setNombre("Partida Test 2");
        sessionFactory.getCurrentSession().save(partida2);

        // Crear rondas de prueba
        ronda1 = new Ronda();
        ronda1.setPartida(partida1);
        ronda1.setPalabra(palabra);
        ronda1.setEstado(Estado.EN_CURSO);
        ronda1.setNumeroDeRonda(1);
        ronda1.setFechaHora(LocalDateTime.now().minusHours(2));

        ronda2 = new Ronda();
        ronda2.setPartida(partida1);
        ronda2.setPalabra(palabra);
        ronda2.setEstado(Estado.FINALIZADA);
        ronda2.setNumeroDeRonda(2);
        ronda2.setFechaHora(LocalDateTime.now().minusHours(1));

        ronda3 = new Ronda();
        ronda3.setPartida(partida2);
        ronda3.setPalabra(palabra);
        ronda3.setEstado(Estado.EN_CURSO);
        ronda3.setNumeroDeRonda(1);
        ronda3.setFechaHora(LocalDateTime.now());

        // Forzar flush para asegurar que se guarden
        sessionFactory.getCurrentSession().flush();
    }

    @Test
    @Rollback
    void deberia_guardar_ronda_correctamente() {
        // Given
        Ronda nuevaRonda = new Ronda();
        nuevaRonda.setPartida(partida1);
        nuevaRonda.setPalabra(palabra);
        nuevaRonda.setEstado(Estado.EN_CURSO);
        nuevaRonda.setNumeroDeRonda(3);
        nuevaRonda.setFechaHora(LocalDateTime.now());

        // When
        Ronda resultado = rondaRepository.guardar(nuevaRonda);

        // Then
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(partida1, resultado.getPartida());
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
        assertEquals(3, resultado.getNumeroDeRonda());
    }

    @Test
    @Rollback
    void deberia_buscar_ronda_por_id() {
        // Given - Guardar una ronda primero
        rondaRepository.guardar(ronda1);
        sessionFactory.getCurrentSession().flush();
        Long id = ronda1.getId();

        // When
        Ronda resultado = rondaRepository.buscarPorId(id);

        // Then
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals(partida1, resultado.getPartida());
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
    }

    @Test
    @Rollback
    void deberia_retornar_null_cuando_no_encuentra_ronda_por_id() {
        // Given
        Long idInexistente = 999L;

        // When
        Ronda resultado = rondaRepository.buscarPorId(idInexistente);

        // Then
        assertNull(resultado);
    }

    @Test
    @Rollback
    void deberia_buscar_rondas_por_partida_ordenadas_por_numero() {
        // Given
        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();

        // When
        List<Ronda> resultado = rondaRepository.buscarPorPartida(partida1);

        // Then
        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getNumeroDeRonda());
        assertEquals(2, resultado.get(1).getNumeroDeRonda());
        // Verificar que todas pertenecen a la partida correcta
        resultado.forEach(ronda -> assertEquals(partida1, ronda.getPartida()));
    }

    @Test
    @Rollback
    void deberia_buscar_rondas_por_estado() {
        // Given
        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();

        // When
        List<Ronda> rondasEnCurso = rondaRepository.buscarPorEstado(Estado.EN_CURSO);
        List<Ronda> rondasFinalizadas = rondaRepository.buscarPorEstado(Estado.FINALIZADA);

        // Then
        assertEquals(2, rondasEnCurso.size());
        assertEquals(1, rondasFinalizadas.size());

        rondasEnCurso.forEach(ronda -> assertEquals(Estado.EN_CURSO, ronda.getEstado()));
        rondasFinalizadas.forEach(ronda -> assertEquals(Estado.FINALIZADA, ronda.getEstado()));
    }

    @Test
    @Rollback
    void deberia_buscar_rondas_por_partida_y_estado() {
        // Given
        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();

        // When
        List<Ronda> resultado = rondaRepository.buscarPorPartidaYEstado(partida1, Estado.EN_CURSO);

        // Then
        assertEquals(1, resultado.size());
        assertEquals(partida1, resultado.get(0).getPartida());
        assertEquals(Estado.EN_CURSO, resultado.get(0).getEstado());
    }

    @Test
    @Rollback
    void deberia_buscar_ultima_ronda_de_partida() {
        // Given
        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        sessionFactory.getCurrentSession().flush();

        // When
        Ronda ultimaRonda = rondaRepository.buscarUltimaRondaDePartida(partida1);

        // Then
        assertNotNull(ultimaRonda);
        assertEquals(2, ultimaRonda.getNumeroDeRonda());
        assertEquals(partida1, ultimaRonda.getPartida());
    }

    @Test
    @Rollback
    void deberia_retornar_null_cuando_partida_no_tiene_rondas() {
        // Given
        Partida2 partidaSinRondas = new Partida2();
        partidaSinRondas.setNombre("Partida Sin Rondas");
        sessionFactory.getCurrentSession().save(partidaSinRondas);

        // When
        Ronda resultado = rondaRepository.buscarUltimaRondaDePartida(partidaSinRondas);

        // Then
        assertNull(resultado);
    }

    @Test
    @Rollback
    void deberia_buscar_todas_las_rondas_ordenadas_por_fecha_desc() {
        // Given
        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();

        // When
        List<Ronda> todasLasRondas = rondaRepository.buscarTodasLasRondas();

        // Then
        assertEquals(3, todasLasRondas.size());
        // Verificar que están ordenadas por fecha descendente
        assertTrue(todasLasRondas.get(0).getFechaHora().isAfter(todasLasRondas.get(1).getFechaHora()) ||
                todasLasRondas.get(0).getFechaHora().isEqual(todasLasRondas.get(1).getFechaHora()));
    }

    @Test
    @Rollback
    void deberia_contar_rondas_de_partida() {
        // Given
        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();

        // When
        Long cantidadPartida1 = rondaRepository.contarRondasDePartida(partida1);
        Long cantidadPartida2 = rondaRepository.contarRondasDePartida(partida2);

        // Then
        assertEquals(2L, cantidadPartida1);
        assertEquals(1L, cantidadPartida2);
    }

    @Test
    @Rollback
    void deberia_eliminar_ronda() {
        // Given
        rondaRepository.guardar(ronda1);
        sessionFactory.getCurrentSession().flush();
        Long id = ronda1.getId();

        // When
        rondaRepository.eliminar(ronda1);
        sessionFactory.getCurrentSession().flush();

        // Then
        Ronda rondaEliminada = rondaRepository.buscarPorId(id);
        assertNull(rondaEliminada);
    }

    @Test
    @Rollback
    void deberia_actualizar_ronda() {
        // Given
        rondaRepository.guardar(ronda1);
        sessionFactory.getCurrentSession().flush();
        Long id = ronda1.getId();

        // When
        ronda1.setEstado(Estado.FINALIZADA);
        ronda1.setNumeroDeRonda(5);
        rondaRepository.actualizar(ronda1);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear(); // Limpiar cache

        // Then
        Ronda rondaActualizada = rondaRepository.buscarPorId(id);
        assertNotNull(rondaActualizada);
        assertEquals(Estado.FINALIZADA, rondaActualizada.getEstado());
        assertEquals(5, rondaActualizada.getNumeroDeRonda());
    }

    @Test
    @Rollback
    void deberia_retornar_lista_vacia_cuando_no_hay_rondas_con_criterio() {
        // Given - No hay rondas guardadas

        // When
        List<Ronda> rondasPorEstado = rondaRepository.buscarPorEstado(Estado.EN_CURSO);
        List<Ronda> rondasPorPartida = rondaRepository.buscarPorPartida(partida1);
        List<Ronda> todasLasRondas = rondaRepository.buscarTodasLasRondas();

        // Then
        assertTrue(rondasPorEstado.isEmpty());
        assertTrue(rondasPorPartida.isEmpty());
        assertTrue(todasLasRondas.isEmpty());
    }

    @Test
    @Rollback
    void deberia_retornar_cero_cuando_partida_no_tiene_rondas_para_contar() {
        // Given
        Partida2 partidaSinRondas = new Partida2();
        partidaSinRondas.setNombre("Partida Sin Rondas");
        sessionFactory.getCurrentSession().save(partidaSinRondas);

        // When
        Long cantidad = rondaRepository.contarRondasDePartida(partidaSinRondas);

        // Then
        assertEquals(0L, cantidad);
    }
}