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
    private SessionFactory sessionFactory;





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
    public void sumarPuntaje(Long usuarioId, Long partidaId, int puntos) {
        UsuarioPartida up = (UsuarioPartida) sessionFactory.getCurrentSession()
                .createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario.id", usuarioId))
                .add(Restrictions.eq("partida.id", partidaId))
                .uniqueResult();

        if (up != null) {
            int puntajeActual = up.getPuntaje();
            up.setPuntaje(puntajeActual + puntos);
            sessionFactory.getCurrentSession().update(up);
        } else {
            // Podés lanzar una excepción si se espera que siempre exista
            throw new IllegalArgumentException("No se encontró UsuarioPartida para el usuarioId " + usuarioId + " y partidaId " + partidaId);
        }
    }

    @Override
    public Usuario obtenerUsuarioPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId) {
        return sessionFactory.getCurrentSession()
                .createQuery(
                        "SELECT up.usuario FROM UsuarioPartida up " +
                                "JOIN FETCH up.usuario u " +
                                "JOIN up.partida p " +
                                "WHERE u.id = :usuarioId AND p.id = :partidaId",
                        Usuario.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("partidaId", partidaId)
                .uniqueResult();
    }

}
