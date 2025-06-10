package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PartidaRepositoryImpl implements PartidaRepository {

    private final SessionFactory sessionFactory;

    public PartidaRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Partida2 partida) {
        sessionFactory.getCurrentSession().save(partida);
    }

    @Override
    public void borrar(Partida2 partida) {
        sessionFactory.getCurrentSession().delete(partida);
    }

    @Override
    public Partida2 buscarPorId(Long partidaId) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "FROM Partida2 p WHERE p.id = :partidaId",
                        Partida2.class)
                .setParameter("partidaId", partidaId)
                .uniqueResult();
    }

    @Override
    public void actualizarEstado(Long partidaId, Estado nuevoEstado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE Partida2 p SET p.estado = :estado WHERE p.id = :id")
                .setParameter("estado", nuevoEstado)
                .setParameter("id", partidaId)
                .executeUpdate();
    }
}
