package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.Partida;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SimpMessagingMockConfigTest;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


import com.tallerwebi.infraestructura.UsuarioPartidaRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        SpringWebTestConfig.class,
        HibernateTestConfig.class,
        SimpMessagingMockConfigTest.class
})
public class UsuarioPartidaRepositoryTest {

    @Autowired
    private UsuarioPartidaRepositoryImpl usuarioPartidaRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private Usuario crearYGuardarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("JugadorTest");
        sessionFactory.getCurrentSession().save(usuario);
        return usuario;
    }

    private Partida crearYGuardarPartida() {
        Partida partida = new Partida();
        partida.setNombre("Partida de test");
        sessionFactory.getCurrentSession().save(partida);
        return partida;
    }

    private UsuarioPartida crearYGuardarRelacion(Usuario usuario, boolean gano, int puntaje) {
        Partida partida = crearYGuardarPartida();  // <-- clave: crear partida y guardar antes

        UsuarioPartida up = new UsuarioPartida();
        up.setUsuario(usuario);
        up.setPartida(partida);   // <-- setear la partida!
        up.setGano(gano);
        up.setPuntaje(puntaje);
        sessionFactory.getCurrentSession().save(up);
        return up;
    }

    @Test
    @Transactional
    @Rollback
    public void testCantidadDePartidasDeJugador() {
        Usuario usuario = crearYGuardarUsuario();
        crearYGuardarRelacion(usuario, true, 10);
        crearYGuardarRelacion(usuario, false, 5);

        int cantidad = usuarioPartidaRepository.getCantidadDePartidasDeJugador(usuario);
        assertEquals(2, cantidad);
    }

    @Test
    @Transactional
    @Rollback
    public void testCantidadDePartidasGanadasDeJugador() {
        Usuario usuario = crearYGuardarUsuario();
        crearYGuardarRelacion(usuario, true, 15);
        crearYGuardarRelacion(usuario, false, 8);

        int ganadas = usuarioPartidaRepository.getCantidadDePartidasGanadasDeJugador(usuario);
        assertEquals(1, ganadas);
    }

    @Test
    @Transactional
    @Rollback
    public void testWinrate() {
        Usuario usuario = crearYGuardarUsuario();
        crearYGuardarRelacion(usuario, true, 20);
        crearYGuardarRelacion(usuario, false, 0);
        crearYGuardarRelacion(usuario, true, 30);

        double winrate = usuarioPartidaRepository.getWinrate(usuario);
        assertEquals(66.66, winrate, 0.1);
    }

    @Test
    @Transactional
    @Rollback
    public void testGuardarUsuarioPartida() {
        Usuario usuario = crearYGuardarUsuario();
        Partida partida = crearYGuardarPartida();

        UsuarioPartida up = new UsuarioPartida();
        up.setUsuario(usuario);
        up.setPartida(partida);
        up.setPuntaje(40);
        up.setGano(true);

        usuarioPartidaRepository.guardarUsuarioPartida(up);
        assertNotNull(up.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void testActualizarPuntaje() {
        Usuario usuario = crearYGuardarUsuario();
        UsuarioPartida up = crearYGuardarRelacion(usuario, false, 10);

        usuarioPartidaRepository.actualizarPuntaje(usuario.getId(), up.getPartida().getId(), 99);
        int nuevo = usuarioPartidaRepository.obtenerPuntaje(usuario.getId(), up.getPartida().getId());
        assertEquals(99, nuevo);
    }

    @Test
    @Transactional
    @Rollback
    public void testObtenerPuntaje() {
        Usuario usuario = crearYGuardarUsuario();
        UsuarioPartida up = crearYGuardarRelacion(usuario, true, 77);

        int puntaje = usuarioPartidaRepository.obtenerPuntaje(usuario.getId(), up.getPartida().getId());
        assertEquals(77, puntaje);
    }

    @Test
    @Transactional
    @Rollback
    public void testActualizar() {
        Usuario usuario = crearYGuardarUsuario();
        UsuarioPartida up = crearYGuardarRelacion(usuario, false, 5);
        up.setPuntaje(25);

        usuarioPartidaRepository.actualizar(up);

        int nuevo = usuarioPartidaRepository.obtenerPuntaje(usuario.getId(), up.getPartida().getId());
        assertEquals(25, nuevo);
    }

    @Test
    @Transactional
    @Rollback
    public void testObtenerUsuarioPartidaPorPartida() {
        Usuario usuario = crearYGuardarUsuario();
        UsuarioPartida up = crearYGuardarRelacion(usuario, true, 10);
        Long partidaId = up.getPartida().getId();

        List<UsuarioPartida> lista = usuarioPartidaRepository.obtenerUsuarioPartidaPorPartida(partidaId);
        assertFalse(lista.isEmpty());
    }

    @Test
    @Transactional
    @Rollback
    public void testObtenerUsuarioEspecificoPorPartida() {
        Usuario usuario = crearYGuardarUsuario();
        UsuarioPartida up = crearYGuardarRelacion(usuario, false, 3);

        UsuarioPartida encontrado = usuarioPartidaRepository.obtenerUsuarioEspecificoPorPartida(
                usuario.getId(), up.getPartida().getId());

        assertNotNull(encontrado);
        assertEquals(usuario.getId(), encontrado.getUsuario().getId());
    }

    private Usuario givenDiferentesPartidasPorJugador() {
        Usuario usuario1 = new Usuario("juan");
        Usuario usuario2 = new Usuario("pedro");
        Usuario usuario3 = new Usuario("ivan");
        Partida partida1 = new Partida("encuentro1", "Castellano", true, 5, 2);
        Partida partida = new Partida("encuentro1", "Castellano", true, 5, 2);
        Partida partida3 = new Partida("encuentro1", "Castellano", true, 5, 2);
        UsuarioPartida upartida1 = new UsuarioPartida(usuario1,partida1,  100,true, Estado.FINALIZADA);
        UsuarioPartida upartida2 = new UsuarioPartida(usuario1, partida, 100,true, Estado.FINALIZADA);
        UsuarioPartida upartida3 = new UsuarioPartida(usuario2,partida1, 20,false, Estado.FINALIZADA);
        UsuarioPartida upartida4 = new UsuarioPartida(usuario3,partida1, 100,true, Estado.FINALIZADA);
        UsuarioPartida upartida5 = new UsuarioPartida(usuario2, partida, 20,false, Estado.FINALIZADA);
        UsuarioPartida upartida6 = new UsuarioPartida(usuario3, partida, 20,false, Estado.FINALIZADA);
        sessionFactory.getCurrentSession().save(usuario1);
        sessionFactory.getCurrentSession().save(usuario2);
        sessionFactory.getCurrentSession().save(usuario3);
        sessionFactory.getCurrentSession().save(partida1);
        sessionFactory.getCurrentSession().save(partida);
        sessionFactory.getCurrentSession().save(partida3);
        sessionFactory.getCurrentSession().save(upartida1);
        sessionFactory.getCurrentSession().save(upartida2);
        sessionFactory.getCurrentSession().save(upartida3);
        sessionFactory.getCurrentSession().save(upartida4);
        sessionFactory.getCurrentSession().save(upartida5);
        sessionFactory.getCurrentSession().save(upartida6);
       return usuario1;

    }



    private int whenConsultoPartidasDeUnJugador(Usuario usuario) {

        return usuarioPartidaRepository.getCantidadDePartidasDeJugador(usuario);
    }
    private int whenConsultoPartidasGanadasDeUnJugador(Usuario usuario) {
        return usuarioPartidaRepository.getCantidadDePartidasGanadasDeJugador(usuario);
    }

}


