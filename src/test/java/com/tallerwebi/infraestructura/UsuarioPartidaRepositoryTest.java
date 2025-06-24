package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import com.tallerwebi.integracion.config.HibernateTestConfig;
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

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/*
@ExtendWith(SpringExtension.class)@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class UsuarioPartidaRepositoryTest {
    @Autowired
    SessionFactory sessionFactory;
    @Autowired
    private UsuarioPartidaRepository usuarioPartidaRepository;

    @Test
    @Transactional
    @Rollback
    public void obtenerLaCantidadDePartidasGanadasPorUnJugador(){
        Usuario usuario = givenDiferentesPartidasPorJugador();
        int cantidad = whenConsultoPartidasGanadasDeUnJugador(usuario);
        thenComparoCantidadDePartidas(2, cantidad);
    }
    @Test
    @Transactional
    @Rollback
    public void ObtenerLaCantidadDePartidasJugadasPorUnJugador(){
     Usuario usuario = givenDiferentesPartidasPorJugador();


     int cantidad = whenConsultoPartidasDeUnJugador(usuario);
     thenComparoCantidadDePartidas(2, cantidad);

    }
    @Test
    @Transactional
    @Rollback
    public void ObtenerElWinrateDeUnJugador(){
        Usuario usuario = givenDiferentesPartidasPorJugador();
        double winrate = whenConsultoWinrateDeUnJugador(usuario);
        thenComparoWinrate(100.00, winrate);
    }
    @Test
    @Transactional
    @Rollback
    public void obtenerElRanking(){
        givenDiferentesPartidasPorJugador();
        List<Object[]> ranking= whenConsultoRanking();
        thenComparoRanking(ranking);
    }

    private void thenComparoRanking(List<Object[]> ranking) {
   assertThat(ranking.size(), equalTo(2));
   assertThat(ranking.get(0)[1], equalTo(2L));
        assertThat(ranking.get(0)[0], equalTo("juan"));

    }

    private List<Object[]> whenConsultoRanking() {
    return usuarioPartidaRepository.obtenerRanking();

    }


    private double whenConsultoWinrateDeUnJugador(Usuario usuario) {
        return usuarioPartidaRepository.getWinrate(usuario);
    }


    private void thenComparoWinrate(double v, double winrate) {
        assertThat(v, equalTo(winrate));

    }

    private void thenComparoCantidadDePartidas(int i, int cantidad) {
    assertThat(i, equalTo(cantidad));
    }


    private Usuario givenDiferentesPartidasPorJugador() {
        Usuario usuario1 = new Usuario("juan");
        Usuario usuario2 = new Usuario("pedro");
        Usuario usuario3 = new Usuario("ivan");
        Partida2 partida1 = new Partida2("encuentro1", "Castellano", true, 5, 2);
        Partida2 partida2 = new Partida2("encuentro1", "Castellano", true, 5, 2);
        Partida2 partida3 = new Partida2("encuentro1", "Castellano", true, 5, 2);
        UsuarioPartida upartida1 = new UsuarioPartida(usuario1,partida1, true);
        UsuarioPartida upartida2 = new UsuarioPartida(usuario1,partida2, true);
        UsuarioPartida upartida3 = new UsuarioPartida(usuario2,partida1, false);
        UsuarioPartida upartida4 = new UsuarioPartida(usuario3,partida1, true);
        UsuarioPartida upartida5 = new UsuarioPartida(usuario2,partida2, false);
        UsuarioPartida upartida6 = new UsuarioPartida(usuario3,partida2, false);
        sessionFactory.getCurrentSession().save(usuario1);
        sessionFactory.getCurrentSession().save(usuario2);
        sessionFactory.getCurrentSession().save(usuario3);
        sessionFactory.getCurrentSession().save(partida1);
        sessionFactory.getCurrentSession().save(partida2);
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

 */
