package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        SpringWebTestConfig.class,
        HibernateTestConfig.class,
        SimpMessagingMockConfigTest.class
})
public class RondaRepositoryTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private RondaRepository rondaRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    @Test
    @Transactional
    @Rollback
    public void queSePuedaObtenerCantidadDeRondasPorPartida() {
        // Crear una partida
        Partida2 partida = new Partida2("Sala Test", "Español", true, 5, 6, 2, Estado.EN_ESPERA);
        sessionFactory.getCurrentSession().save(partida);

        // Crear 3 rondas asociadas a la partida
        Ronda ronda1 = new Ronda(partida);
        Ronda ronda2 = new Ronda(partida);
        Ronda ronda3 = new Ronda(partida);

        sessionFactory.getCurrentSession().save(ronda1);
        sessionFactory.getCurrentSession().save(ronda2);
        sessionFactory.getCurrentSession().save(ronda3);

        // Ejecutar el método y verificar el resultado
        int cantidad = rondaRepository.obtenerCantidadDeRondasPorPartida(partida.getId());

        assertEquals(3, cantidad);
    }
}

