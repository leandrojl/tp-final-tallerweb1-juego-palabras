package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.dominio.model.Partida;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
public class PartidaRepositoryImpl implements PartidaRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Partida buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Partida.class, id);
    }

    // Ejemplo: guardar una partida (por si querés usarlo después)
    @Override
    public void guardar(Partida partida) {
        sessionFactory.getCurrentSession().save(partida);
    }

    @Override
    public Serializable crearPartida(Partida nuevaPartida) {
        return sessionFactory.getCurrentSession().save(nuevaPartida);
    }

    @Override
    public void actualizarEstado(Long idPartida, Estado estado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE Partida p SET p.estado = :estado WHERE p.id = :idPartida")
                .setParameter("idPartida", idPartida)
                .setParameter("estado",estado)
                .executeUpdate();

    }

    @Override
    public void cancelarPartidaDeUsuario(Long idUsuario, Long idPartida) {
        Session session = sessionFactory.getCurrentSession();
        int updated = session.createQuery(
                        "UPDATE Partida p SET p.estado = :estado WHERE p.id = :idPartida")
                .setParameter("estado", Estado.CANCELADA)
                .setParameter("idPartida", idPartida)
                .executeUpdate();

        if (updated == 0) {
            throw new IllegalArgumentException("No se encontró la partida con id " + idPartida);
        }
    }

    @Override
    public boolean verificarSiEsElCreadorDePartida(Long idUsuario, Long idPartida) {
        Partida partida = sessionFactory.getCurrentSession().get(Partida.class, idPartida);
        if (partida == null) {
            return false;
        }
        return idUsuario != null && idUsuario.equals(partida.getCreadorId());
    }

    @Override
    public Estado verificarEstadoDeLaPartida(Long idPartida) {
        Partida partida = sessionFactory.getCurrentSession().get(Partida.class, idPartida);
        if (partida == null) {
            throw new IllegalArgumentException("No se encontró la partida con id " + idPartida);
        }
        return partida.getEstado();
    }

    @Override
    public Partida obtenerPartidaAleatoria() {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery(
                        "SELECT p FROM Partida p " +
                                "WHERE p.id IN (" +
                                "   SELECT up.partida.id FROM UsuarioPartida up " +
                                "   GROUP BY up.partida.id " +
                                "   HAVING COUNT(up.id) < (" +
                                "       SELECT p2.maximoJugadores FROM Partida p2 WHERE p2.id = up.partida.id" +
                                "   )" +
                                ")",
                        Partida.class
                )
                .setMaxResults(1)
                .uniqueResult();
    }

    public Partida buscarPartidaPorId(Long idPartida) {
        return sessionFactory.getCurrentSession().get(Partida.class, idPartida);
    }

    @Override
    public void modificarEstadoPartida(Partida partida, Estado estado) {
        partida.setEstado(estado);
        sessionFactory.getCurrentSession().update(partida);
    }

    @Override
    public String obtenerNombrePartidaPorId(Long idPartida) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT p.nombre FROM Partida p WHERE p.id = :idPartida", String.class)
                .setParameter("idPartida", idPartida)
                .uniqueResult();
    }

}
