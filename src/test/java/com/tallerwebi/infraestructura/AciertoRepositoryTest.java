package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.interfaceRepository.AciertoRepository;
import com.tallerwebi.dominio.model.Acierto;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SimpMessagingMockConfigTest;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
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
@ContextConfiguration(classes = {
        SpringWebTestConfig.class,
        HibernateTestConfig.class,
        SimpMessagingMockConfigTest.class
})
public class AciertoRepositoryTest {

    @Autowired
    private AciertoRepository aciertoRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private Usuario usuario;
    private Ronda ronda;

    @BeforeEach
    @Transactional
    @Rollback
    public void setUp() {
        var session = sessionFactory.getCurrentSession();

        usuario = new Usuario();
        usuario.setNombreUsuario("JugadorTest");
        session.save(usuario);

        ronda = new Ronda();
        ronda.setNumeroDeRonda(1);
        session.save(ronda);

        Acierto acierto = new Acierto();
        acierto.setUsuario(usuario);
        acierto.setRonda(ronda);
        acierto.setOrdenDeAcierto(1);
        session.save(acierto);
    }

    @Test
    @Transactional
    @Rollback
    public void debeContarUsuariosQueAcertaron() {
        long total = aciertoRepository.contarUsuariosQueAcertaron(ronda.getId());
        assertEquals(1, total);
    }

    @Test
    @Transactional
    @Rollback
    public void debeDetectarSiJugadorYaAcerto() {
        boolean acerto = aciertoRepository.jugadorYaAcerto(usuario.getId(), ronda.getId());
        assertTrue(acerto);
    }

    @Test
    @Transactional
    @Rollback
    public void debeRegistrarAciertoNuevo() {
        Usuario otro = new Usuario();
        otro.setNombreUsuario("OtroUsuario");
        sessionFactory.getCurrentSession().save(otro);

        Acierto nuevo = new Acierto();
        nuevo.setUsuario(otro);
        nuevo.setRonda(ronda);
        nuevo.setOrdenDeAcierto(2);

        aciertoRepository.registrarAcierto(nuevo);

        long total = aciertoRepository.contarUsuariosQueAcertaron(ronda.getId());
        assertEquals(2, total);
    }

    @Test
    @Transactional
    @Rollback
    public void debeContarAciertosPorRonda() {
        int cantidad = aciertoRepository.contarAciertosPorRondaId(ronda.getId());
        assertEquals(1, cantidad);
    }

    @Test
    @Transactional
    @Rollback
    public void jugadorYaAcertoDebeDevolverFalseSiNoExiste() {
        Usuario otro = new Usuario();
        otro.setNombreUsuario("UsuarioNuevo");
        sessionFactory.getCurrentSession().save(otro);

        boolean resultado = aciertoRepository.jugadorYaAcerto(otro.getId(), ronda.getId());

        assertFalse(resultado);
    }

}
