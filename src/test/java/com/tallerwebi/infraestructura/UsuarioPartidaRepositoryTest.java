package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Partida2;
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

    private Partida2 crearYGuardarPartida() {
        Partida2 partida = new Partida2();
        partida.setNombre("Partida de test");
        sessionFactory.getCurrentSession().save(partida);
        return partida;
    }

    private UsuarioPartida crearYGuardarRelacion(Usuario usuario, boolean gano, int puntaje) {
        Partida2 partida = crearYGuardarPartida();  // <-- clave: crear partida y guardar antes

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
        Partida2 partida = crearYGuardarPartida();

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
}


