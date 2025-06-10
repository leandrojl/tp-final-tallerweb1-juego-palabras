package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.RondaRepositoryImpl;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
class RondaRepositoryImplTest {

    @Autowired
    private SessionFactory sessionFactory;

    private RondaRepositoryImpl rondaRepository;
    private Partida2 partida1;
    private Partida2 partida2;
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

        // Flush para asegurar que las entidades tengan ID
        sessionFactory.getCurrentSession().flush();
    }

    // Método helper para crear rondas
    private Ronda crearRonda(Partida2 partida, Estado estado, int numeroRonda, int horasAtras) {
        Ronda ronda = new Ronda();
        ronda.setPartida(partida);
        ronda.setPalabra(palabra); // Ahora puede reutilizar la misma palabra con @ManyToOne
        ronda.setEstado(estado);
        ronda.setNumeroDeRonda(numeroRonda);
        ronda.setFechaHora(LocalDateTime.now().minusHours(horasAtras));
        return ronda;
    }

    @Test
    @Rollback
    @Transactional
    void deberia_guardar_ronda_correctamente() {
        Ronda nuevaRonda = crearRonda(partida1, Estado.EN_CURSO, 3, 0);

        Ronda resultado = rondaRepository.guardar(nuevaRonda);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(partida1.getId(), resultado.getPartida().getId());
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
        assertEquals(3, resultado.getNumeroDeRonda());
    }

    @Test
    @Rollback
    @Transactional
    void deberia_buscar_ronda_por_id() {
        Ronda ronda = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        rondaRepository.guardar(ronda);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Long id = ronda.getId();

        Ronda resultado = rondaRepository.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals(partida1.getId(), resultado.getPartida().getId());
        assertEquals(Estado.EN_CURSO, resultado.getEstado());
    }

    @Test
    @Rollback
    @Transactional
    void deberia_retornar_null_cuando_no_encuentra_ronda_por_id() {
        Long idInexistente = 999L;

        Ronda resultado = rondaRepository.buscarPorId(idInexistente);

        assertNull(resultado);
    }

    @Test
    @Rollback
    @Transactional
    void deberia_buscar_rondas_por_partida_ordenadas_por_numero() {
        Ronda ronda1 = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        Ronda ronda2 = crearRonda(partida1, Estado.FINALIZADA, 2, 1);
        Ronda ronda3 = crearRonda(partida2, Estado.EN_CURSO, 1, 0);

        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        List<Ronda> resultado = rondaRepository.buscarPorPartida(partida1);

        assertEquals(2, resultado.size());
        assertEquals(1, resultado.get(0).getNumeroDeRonda());
        assertEquals(2, resultado.get(1).getNumeroDeRonda());
        resultado.forEach(ronda -> assertEquals(partida1.getId(), ronda.getPartida().getId()));
    }

    @Test
    @Rollback
    @Transactional
    void deberia_buscar_rondas_por_estado() {
        Ronda ronda1 = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        Ronda ronda2 = crearRonda(partida1, Estado.FINALIZADA, 2, 1);
        Ronda ronda3 = crearRonda(partida2, Estado.EN_CURSO, 1, 0);

        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        List<Ronda> rondasEnCurso = rondaRepository.buscarPorEstado(Estado.EN_CURSO);
        List<Ronda> rondasFinalizadas = rondaRepository.buscarPorEstado(Estado.FINALIZADA);

        assertEquals(2, rondasEnCurso.size());
        assertEquals(1, rondasFinalizadas.size());

        rondasEnCurso.forEach(ronda -> assertEquals(Estado.EN_CURSO, ronda.getEstado()));
        rondasFinalizadas.forEach(ronda -> assertEquals(Estado.FINALIZADA, ronda.getEstado()));
    }

    @Test
    @Rollback
    @Transactional
    void deberia_buscar_rondas_por_partida_y_estado() {
        Ronda ronda1 = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        Ronda ronda2 = crearRonda(partida1, Estado.FINALIZADA, 2, 1);
        Ronda ronda3 = crearRonda(partida2, Estado.EN_CURSO, 1, 0);

        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        List<Ronda> resultado = rondaRepository.buscarPorPartidaYEstado(partida1, Estado.EN_CURSO);

        assertEquals(1, resultado.size());
        assertEquals(partida1.getId(), resultado.get(0).getPartida().getId());
        assertEquals(Estado.EN_CURSO, resultado.get(0).getEstado());
    }

    @Test
    @Rollback
    @Transactional
    void deberia_buscar_ultima_ronda_de_partida() {
        Ronda ronda1 = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        Ronda ronda2 = crearRonda(partida1, Estado.FINALIZADA, 2, 1);

        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Ronda ultimaRonda = rondaRepository.buscarUltimaRondaDePartida(partida1);

        assertNotNull(ultimaRonda);
        assertEquals(2, ultimaRonda.getNumeroDeRonda());
        assertEquals(partida1.getId(), ultimaRonda.getPartida().getId());
    }

    @Test
    @Rollback
    @Transactional
    void deberia_retornar_null_cuando_partida_no_tiene_rondas() {
        Partida2 partidaSinRondas = new Partida2();
        partidaSinRondas.setNombre("Partida Sin Rondas");
        sessionFactory.getCurrentSession().save(partidaSinRondas);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Ronda resultado = rondaRepository.buscarUltimaRondaDePartida(partidaSinRondas);

        assertNull(resultado);
    }

    @Test
    @Rollback
    @Transactional
    void deberia_buscar_todas_las_rondas_ordenadas_por_fecha_desc() {
        Ronda ronda1 = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        Ronda ronda2 = crearRonda(partida1, Estado.FINALIZADA, 2, 1);
        Ronda ronda3 = crearRonda(partida2, Estado.EN_CURSO, 1, 0);

        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        List<Ronda> todasLasRondas = rondaRepository.buscarTodasLasRondas();

        assertEquals(3, todasLasRondas.size());
        assertTrue(todasLasRondas.get(0).getFechaHora().isAfter(todasLasRondas.get(1).getFechaHora()) ||
                todasLasRondas.get(0).getFechaHora().isEqual(todasLasRondas.get(1).getFechaHora()));
    }

    @Test
    @Rollback
    @Transactional
    void deberia_contar_rondas_de_partida() {
        Ronda ronda1 = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        Ronda ronda2 = crearRonda(partida1, Estado.FINALIZADA, 2, 1);
        Ronda ronda3 = crearRonda(partida2, Estado.EN_CURSO, 1, 0);

        rondaRepository.guardar(ronda1);
        rondaRepository.guardar(ronda2);
        rondaRepository.guardar(ronda3);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Long cantidadPartida1 = rondaRepository.contarRondasDePartida(partida1);
        Long cantidadPartida2 = rondaRepository.contarRondasDePartida(partida2);

        assertEquals(2L, cantidadPartida1);
        assertEquals(1L, cantidadPartida2);
    }

    @Test
    @Rollback
    @Transactional
    void deberia_eliminar_ronda() {
        Ronda ronda = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        rondaRepository.guardar(ronda);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Long id = ronda.getId();

        rondaRepository.eliminar(ronda);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Ronda rondaEliminada = rondaRepository.buscarPorId(id);
        assertNull(rondaEliminada);
    }

    @Test
    @Rollback
    @Transactional
    void deberia_actualizar_ronda() {
        Ronda ronda = crearRonda(partida1, Estado.EN_CURSO, 1, 2);
        rondaRepository.guardar(ronda);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Long id = ronda.getId();

        ronda.setEstado(Estado.FINALIZADA);
        ronda.setNumeroDeRonda(5);
        rondaRepository.actualizar(ronda);

        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Ronda rondaActualizada = rondaRepository.buscarPorId(id);
        assertNotNull(rondaActualizada);
        assertEquals(Estado.FINALIZADA, rondaActualizada.getEstado());
        assertEquals(5, rondaActualizada.getNumeroDeRonda());
    }

    @Test
    @Rollback
    @Transactional
    void deberia_retornar_lista_vacia_cuando_no_hay_rondas_con_criterio() {
        List<Ronda> rondasPorEstado = rondaRepository.buscarPorEstado(Estado.EN_CURSO);
        List<Ronda> rondasPorPartida = rondaRepository.buscarPorPartida(partida1);
        List<Ronda> todasLasRondas = rondaRepository.buscarTodasLasRondas();

        assertTrue(rondasPorEstado.isEmpty());
        assertTrue(rondasPorPartida.isEmpty());
        assertTrue(todasLasRondas.isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    void deberia_retornar_cero_cuando_partida_no_tiene_rondas_para_contar() {
        Partida2 partidaNueva = new Partida2();
        partidaNueva.setNombre("Partida Nueva");
        sessionFactory.getCurrentSession().save(partidaNueva);
        sessionFactory.getCurrentSession().flush();
        sessionFactory.getCurrentSession().clear();

        Long cantidad = rondaRepository.contarRondasDePartida(partidaNueva);
        assertEquals(0L, cantidad);
    }
}
