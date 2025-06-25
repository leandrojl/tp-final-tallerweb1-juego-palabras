package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioPartidaRepositoryImpl implements UsuarioPartidaRepository {

    @Autowired
    SessionFactory sessionFactory;





    @Override
    public int getCantidadDePartidasDeJugador(Usuario usuario) {

        Long resultado = (long) sessionFactory.getCurrentSession().createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario", usuario))
                .setProjection(Projections.rowCount())
                .uniqueResult();
        int cantidad = (resultado != null) ? resultado.intValue() : 0;
        return cantidad;
    }

    @Override
    public int getCantidadDePartidasGanadasDeJugador(Usuario usuario) {
        Long resultado = (long) sessionFactory.getCurrentSession().createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario", usuario))
                .add(Restrictions.eq("gano", true))
                .setProjection(Projections.rowCount())
                .uniqueResult();
        int cantidad = (resultado != null) ? resultado.intValue() : 0;
        return cantidad;


    }

    @Override
    public double getWinrate(Usuario usuario) {
        int partidasJugadas = getCantidadDePartidasDeJugador(usuario);
        int partidasGanadas = getCantidadDePartidasGanadasDeJugador(usuario);

        return partidasJugadas > 0 ? partidasGanadas * 100.0 / partidasJugadas : 0.0;
    }

    @Override
    public List<Object[]> obtenerRanking() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "SELECT u.nombreUsuario, COUNT(p) " +
                        "FROM UsuarioPartida p " +
                        "JOIN p.usuario u " +
                        "WHERE p.gano = true " +
                        "GROUP BY u.nombreUsuario " +
                        "ORDER BY COUNT(p) DESC", Object[].class
        ).getResultList();
    }


    @Override
    public void guardarUsuarioPartida(UsuarioPartida usuarioPartida) {
        sessionFactory.getCurrentSession().save(usuarioPartida);
    }


    @Override
    public void actualizarPuntaje(Long usuarioId, Long partidaId, int nuevoPuntaje) {
        UsuarioPartida up = (UsuarioPartida) sessionFactory.getCurrentSession()
                .createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("partida.id", partidaId))
                .uniqueResult();

        if (up != null) {
            up.setPuntaje(nuevoPuntaje);
            sessionFactory.getCurrentSession().update(up);

        }


    }

    @Override
    public int obtenerPuntaje(Long usuarioId, Long partidaId) {
        UsuarioPartida up = (UsuarioPartida) sessionFactory.getCurrentSession()
                .createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("partida.id", partidaId))
                .uniqueResult();

        return up != null ? up.getPuntaje() : 0;
    }
    @Override
    public List<UsuarioPartida> obtenerUsuarioPartidaPorPartida(Long partidaId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("partida.id", partidaId))
                .list();
    }

    @Override
    public UsuarioPartida obtenerUsuarioEspecificoPorPartida(Long usuarioId, Long partidaId) {
        Session session = sessionFactory.getCurrentSession();

        return (UsuarioPartida) session.createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("partida.id", partidaId))
                .uniqueResult();  // único resultado esperado
    }

    @Override
    public void actualizar(UsuarioPartida relacion) {
        Session session = sessionFactory.getCurrentSession();
        session.update(relacion);
    }

}
