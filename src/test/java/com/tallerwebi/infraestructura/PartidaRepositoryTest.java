package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.dominio.model.Partida;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class, SimpMessagingMockConfigTest.class})
public class PartidaRepositoryTest {

    @Autowired
    private PartidaRepository partidaRepository;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @Transactional
    @Rollback
    public void testCrearPartidaPersisteEnBase() {
        Partida partida = new Partida("Sala Test", "Español", true, 5, 6, 2, Estado.EN_ESPERA);
        partidaRepository.crearPartida(partida);
        assertNotNull(partida.getId());
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaActualizarElEstadoDeUnaPartida(){
        Partida partida = givenExistePartida();

        assertTrue(partida.getEstado().equals(Estado.EN_ESPERA));
        Partida partidaActualizada = whenActualizoEstadoDeUnaPartida(partida);


        assertTrue(partidaActualizada.getEstado().equals(Estado.EN_CURSO));
    }

    private Partida whenActualizoEstadoDeUnaPartida(Partida partida) {
        partidaRepository.actualizarEstado(partida.getId(), Estado.EN_CURSO);
        this.sessionFactory.getCurrentSession().refresh(partida);
        return partidaRepository.buscarPorId(partida.getId());
    }

    private Partida givenExistePartida() {
        Partida partida = new Partida("Sala Test", "Español", true, 5, 6, 2, Estado.EN_ESPERA);
        this.sessionFactory.getCurrentSession().save(partida);
        return partidaRepository.buscarPorId(partida.getId());
    }


}