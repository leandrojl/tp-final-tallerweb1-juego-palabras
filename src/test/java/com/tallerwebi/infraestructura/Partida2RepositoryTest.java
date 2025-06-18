package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.Partida2Repository;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
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
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class Partida2RepositoryTest {

    @Autowired
    private Partida2Repository partida2Repository;


    @Test
    @Transactional
    @Rollback
    public void testCrearPartidaPersisteEnBase() {
        Partida2 partida = new Partida2("Sala Test", "Español", true, 5, 6, 2, Estado.EN_ESPERA);
        partida2Repository.crearPartida(partida);
        assertNotNull(partida.getId());
    }
}