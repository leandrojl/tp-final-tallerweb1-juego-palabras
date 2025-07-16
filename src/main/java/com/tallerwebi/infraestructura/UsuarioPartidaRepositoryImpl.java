package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.Partida;
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
    public int contarUsuariosEnPartida(Long idPartida) {
        Long resultado = (Long) sessionFactory.getCurrentSession()
                .createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("partida.id", idPartida))
                .setProjection(Projections.rowCount())
                .uniqueResult();

        return (resultado != null) ? resultado.intValue() : 0;
    }

    @Override
    public List<UsuarioPartida> buscarPorPartida(Long idPartida) {
        Session session = sessionFactory.getCurrentSession();
        session.flush();  // <-- acá sí funciona
        String hql = "FROM UsuarioPartida up JOIN FETCH up.usuario WHERE up.partida.id = :idPartida";
        return session.createQuery(hql, UsuarioPartida.class)
                .setParameter("idPartida", idPartida)
                .getResultList();
    }

    public List<Usuario> obtenerUsuariosDeUnaPartida(Long idPartida) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createQuery(
                        "SELECT up.usuario FROM UsuarioPartida up WHERE up.partida.id = :idPartida",
                        Usuario.class
                ).setParameter("idPartida", idPartida)
                .getResultList();
    }

    @Override
    public UsuarioPartida obtenerUsuarioPartida(Usuario usuario, Partida partida) {
        Session session = this.sessionFactory.getCurrentSession();

        return session.createQuery(
                        "FROM UsuarioPartida up WHERE up.usuario = :usuario AND up.partida = :partida",
                        UsuarioPartida.class
                )
                .setParameter("usuario", usuario)
                .setParameter("partida", partida)
                .uniqueResult();
    }

    @Override
    public void borrarUsuarioPartidaAsociadaAlUsuario(Long idPartida, Long idUsuario) {
        Session session = this.sessionFactory.getCurrentSession();
        session.createQuery("DELETE FROM UsuarioPartida u " +
                "WHERE u.partida.id = :idPartida AND u.usuario.id = :idUsuario")
                .setParameter("idPartida", idPartida)
                .setParameter("idUsuario", idUsuario)
                .executeUpdate();
    }

    @Override
    public Partida obtenerPartida(Long idPartida) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Partida) session.createQuery(
                        "SELECT p FROM UsuarioPartida up JOIN up.partida p WHERE p.id = :idPartida"
                )
                .setParameter("idPartida", idPartida)
                .uniqueResult();
    }


    @Override
    public void agregarUsuarioAPartida(Long idUsuario,
                                       Long idPartida,
                                       int puntaje,
                                       boolean gano,
                                       Estado estado) {

        Session session = sessionFactory.getCurrentSession();

        Usuario usuario = session.get(Usuario.class, idUsuario);
        Partida partida = session.get(Partida.class, idPartida);

        if (usuario != null && partida != null) {
            UsuarioPartida usuarioPartida = new UsuarioPartida();
            usuarioPartida.setUsuario(usuario);
            usuarioPartida.setPartida(partida);
            usuarioPartida.setGano(gano);
            usuarioPartida.setPuntaje(puntaje);
            usuarioPartida.setEstado(estado);

            session.save(usuarioPartida);
        }
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
            throw new IllegalArgumentException("No se encontró UsuarioPartida para el idUsuario " + usuarioId + " y partidaId " + partidaId);

        }
    }

    @Override
    public String obtenerNombreDeUsuarioEnLaPartida(Long usuarioId, Long idPartida) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT up.usuario.nombreUsuario FROM UsuarioPartida up WHERE up.usuario.id = :usuarioId AND up.partida.id = :idPartida", String.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("idPartida", idPartida)
                .uniqueResult();
    }

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

    @Override
    public UsuarioPartida obtenerUsuarioPartida(Long idUsuario, Long idPartida) {
        Session session = sessionFactory.getCurrentSession();
        return  session.createQuery("SELECT up FROM UsuarioPartida up" +
                " WHERE up.partida.id = :idpartida AND up.usuario.id = :idUsuario", UsuarioPartida.class)
                .setParameter("idpartida", idPartida)
                .setParameter("idUsuario", idUsuario)
                .uniqueResult();
    } // LE TENGO QUE HACER TEST EN REPOTEST

    @Override
    public void cancelarPartidaDeUsuario(Long idUsuario, Long idPartida) {
        Session session = sessionFactory.getCurrentSession();
        int updated = session.createQuery(
                        "UPDATE UsuarioPartida up SET up.estado = :estado " +
                                "WHERE up.usuario.id = :idUsuario AND up.partida.id = :idPartida")
                .setParameter("estado", Estado.CANCELADA)
                .setParameter("idUsuario", idUsuario)
                .setParameter("idPartida", idPartida)
                .executeUpdate();

        if (updated == 0) {
            throw new IllegalArgumentException("No se encontró UsuarioPartida para el usuarioId " + idUsuario + " y partidaId " + idPartida);
        }
    }

    public void actualizarEstado(Long idPartida, Estado estado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE UsuarioPartida up SET up.estado = :estado " +
                "WHERE up.partida.id = :idPartida")
                .setParameter("idPartida", idPartida)
                .setParameter("estado", estado)
                .executeUpdate();

    }

    @Override
    public List<Usuario> obtenerUsuariosListosDeUnaPartida(Long idPartida) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createQuery(
                        "SELECT up.usuario FROM UsuarioPartida up WHERE up.partida.id = :idPartida AND up.usuario.estaListo = true",
                        Usuario.class
                ).setParameter("idPartida", idPartida)
                .getResultList();
    }

    @Override
    public Usuario obtenerUsuarioDeUnaPartidaPorSuNombreUsuario(String nombreUsuarioDelPrincipal, Long idPartida) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT up.usuario FROM UsuarioPartida up" +
                        " WHERE up.partida.id = :idPartida AND up.usuario.nombreUsuario = :nombreUsuario", Usuario.class)
                .setParameter("nombreUsuario", nombreUsuarioDelPrincipal)
                .setParameter("idPartida", idPartida)
                .uniqueResult();

    }

    @Override
    public void cambiarEstado(Long idUsuarioAExpulsar, Long idPartida, Estado estado) {
        Session session = sessionFactory.getCurrentSession();
        int updated = session.createQuery(
                        "UPDATE UsuarioPartida up SET up.estado = :estado " +
                                "WHERE up.usuario.id = :idUsuario AND up.partida.id = :idPartida")
                .setParameter("estado", estado)
                .setParameter("idUsuario", idUsuarioAExpulsar)
                .setParameter("idPartida", idPartida)
                .executeUpdate();

        if (updated == 0) {
            throw new IllegalArgumentException("No se encontró UsuarioPartida para el usuarioId " + idUsuarioAExpulsar + " y partidaId " + idPartida);
        }

    }

    @Override
    public int cantidadDeJugadoresActivosEnPartida(Long partidaId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "SELECT COUNT(u) " +
                                "FROM UsuarioPartida u " +
                                "WHERE u.estado = :estado " +
                                "AND u.partida.id = :partidaId", Long.class)
                .setParameter("estado", Estado.EN_CURSO)
                .setParameter("partidaId", partidaId)
                .uniqueResult()
                .intValue();
    }

    @Override
    public void finalizarPartidaParaTodos(Long partidaId, Estado estado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE UsuarioPartida up SET up.estado = :estado WHERE up.partida.id = :partidaId")
                .setParameter("estado", estado)
                .setParameter("partidaId", partidaId)
                .executeUpdate();
    }

    @Override
    public void marcarTodasLasPartidasComoFinalizadas(Long idUsuario, Estado estado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE UsuarioPartida up SET up.estado = :estado WHERE up.usuario.id = :idUsuario AND up.estado = 'EN_CURSO'")
                .setParameter("idUsuario", idUsuario)
                .setParameter("estado", estado)
                .executeUpdate();
    }
}
