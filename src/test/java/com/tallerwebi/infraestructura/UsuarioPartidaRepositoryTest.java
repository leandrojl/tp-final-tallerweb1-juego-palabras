package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SimpMessagingMockConfigTest;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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


    @Test
    @Transactional
    @Rollback
    public void queSePuedanObtenerUsuariosDeUnaPartida() {
        Usuario usuario = new Usuario("pepe");
        Partida partida = givenExistenUsuariosEnPartida(usuario);

        List<Usuario> usuariosEnPartidaObtenidos = whenObtengoUsuariosDeUnaPartida(partida);

        assertNotNull(usuariosEnPartidaObtenidos);
        assertEquals(usuario.getId(), usuariosEnPartidaObtenidos.get(0).getId());
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaObtenerUnUsuarioEnUnaPartidaPorSuNombreUsuario() {
        Usuario usuario = new Usuario("pepe");
        Partida partida = givenExistenUsuariosEnPartida(usuario);

        Usuario usuarioObtenido = whenObtengoUnUsuarioEnUnaPartidaPorSuNombreUsuario(usuario.getNombreUsuario(), partida.getId());

        assertEquals(usuario.getId(), usuarioObtenido.getId());
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaActualizarElEstadoDeUsuarioPartida() {
        UsuarioPartida usuarioPartida = givenExisteUsuarioPartida();

        assertTrue(usuarioPartida.getEstado().equals(Estado.EN_ESPERA));
        UsuarioPartida usuarioPartidaActualizada = whenActualizoEstadoDeUsuarioPartida(usuarioPartida);

        assertTrue(usuarioPartidaActualizada.getEstado().equals(Estado.EN_CURSO));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaObtenerUnaPartidaPorSuId() {
        UsuarioPartida usuarioPartida = givenExisteUsuarioPartida();
        Partida partida = usuarioPartida.getPartida();

        Partida partidaObtenida = usuarioPartidaRepository.obtenerPartida(partida.getId());

        assertEquals(partida.getId(), partidaObtenida.getId());
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedanObtenerLosUsuariosListosDeUnaPartida() {
        Partida partida = givenExisteUsuarioPartidaConUsuariosListos();

        List<Usuario> usuariosListos = usuarioPartidaRepository.obtenerUsuariosListosDeUnaPartida(partida.getId());

        assertNotNull(usuariosListos);
        assertEquals(2, usuariosListos.size());
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaBorrarUsuarioPartidaAsociadaAUnUsuario() {
        UsuarioPartida usuarioPartida = givenExisteUsuarioPartida();

        assertNotNull(usuarioPartida);
        UsuarioPartida usuarioPartidaObtenido = whenBorroUsuarioPartidaAsociadaAUnUsuario(usuarioPartida);

        assertNull(usuarioPartidaObtenido);
    }



















    private UsuarioPartida whenBorroUsuarioPartidaAsociadaAUnUsuario(UsuarioPartida usuarioPartida) {
        Usuario usuario = usuarioPartida.getUsuario();
        Partida partida = usuarioPartida.getPartida();
        usuarioPartidaRepository.borrarUsuarioPartidaAsociadaAlUsuario(partida.getId(), usuario.getId());
        return usuarioPartidaRepository.obtenerUsuarioPartida(usuario.getId(), partida.getId());
    }



    private Partida givenExisteUsuarioPartidaConUsuariosListos() {
        Usuario usuario = new Usuario("pepe");
        usuario.setEstaListo(true);
        sessionFactory.getCurrentSession().save(usuario);
        Usuario usuario2 = new Usuario("jose");
        usuario2.setEstaListo(true);
        sessionFactory.getCurrentSession().save(usuario2);
        Usuario usuario3 = new Usuario("lucas");
        usuario3.setEstaListo(false);
        sessionFactory.getCurrentSession().save(usuario3);
        Partida partida = new Partida();
        sessionFactory.getCurrentSession().save(partida);


        UsuarioPartida usuarioPartida = new UsuarioPartida(usuario, partida, 0, false, null);
        sessionFactory.getCurrentSession().save(usuarioPartida);
        UsuarioPartida usuarioPartida2 = new UsuarioPartida(usuario2, partida, 0, false, null);
        sessionFactory.getCurrentSession().save(usuarioPartida2);
        UsuarioPartida usuarioPartida3 = new UsuarioPartida(usuario3, partida, 0, false, null);
        sessionFactory.getCurrentSession().save(usuarioPartida3);
        return partida;
    }


    private UsuarioPartida whenActualizoEstadoDeUsuarioPartida(UsuarioPartida usuarioPartida) {
        usuarioPartidaRepository.actualizarEstado(usuarioPartida.getPartida().getId(),Estado.EN_CURSO);
        sessionFactory.getCurrentSession().refresh(usuarioPartida);
        return usuarioPartidaRepository.obtenerUsuarioPartida(usuarioPartida.getUsuario().getId(),usuarioPartida.getPartida().getId());
    }

    private UsuarioPartida givenExisteUsuarioPartida() {
        Usuario usuario = new Usuario("pepe");
        Partida partida = new Partida();
        sessionFactory.getCurrentSession().save(partida);

        sessionFactory.getCurrentSession().save(usuario);
        UsuarioPartida usuarioPartida = new UsuarioPartida(usuario, partida, 0, false, Estado.EN_ESPERA);
        sessionFactory.getCurrentSession().save(usuarioPartida);
        return usuarioPartida;
    }

    private Usuario whenObtengoUnUsuarioEnUnaPartidaPorSuNombreUsuario(String nombreUsuario, Long idUsuario) {
        return usuarioPartidaRepository.obtenerUsuarioDeUnaPartidaPorSuNombreUsuario(nombreUsuario, idUsuario);
    }

    private List<Usuario> whenObtengoUsuariosDeUnaPartida(Partida partida) {
        return usuarioPartidaRepository.obtenerUsuariosDeUnaPartida(partida.getId());
    }

    private Partida givenExistenUsuariosEnPartida(Usuario usuario) {
        Partida partida = new Partida();
        sessionFactory.getCurrentSession().save(partida);

        sessionFactory.getCurrentSession().save(usuario);
        UsuarioPartida usuarioPartida = new UsuarioPartida(usuario, partida, 0, false, null);
        sessionFactory.getCurrentSession().save(usuarioPartida);
        return partida;
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


