package com.tallerwebi.dominio;


import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RondaRepositoryImpl implements RondaRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public RondaRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Ronda guardar(Ronda ronda) {
        sessionFactory.getCurrentSession().saveOrUpdate(ronda);
        return ronda;
    }


    @Override
    public Ronda buscarPorId(Long id) {
        return sessionFactory.getCurrentSession()
                .get(Ronda.class, id);
    }

    @Override
    public List<Ronda> buscarPorPartida(Partida2 partida) {
        String hql = "FROM Ronda r WHERE r.partida = :partida ORDER BY r.numeroDeRonda";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Ronda.class)
                .setParameter("partida", partida)
                .list();
    }

    @Override
    public List<Ronda> buscarPorEstado(Estado estado) {
        String hql = "FROM Ronda r WHERE r.estado = :estado";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Ronda.class)
                .setParameter("estado", estado)
                .list();
    }

    @Override
    public List<Ronda> buscarPorPartidaYEstado(Partida2 partida, Estado estado) {
        String hql = "FROM Ronda r WHERE r.partida = :partida AND r.estado = :estado";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Ronda.class)
                .setParameter("partida", partida)
                .setParameter("estado", estado)
                .list();
    }

    @Override
    public Ronda buscarUltimaRondaDePartida(Partida2 partida) {
        String hql = "FROM Ronda r WHERE r.partida = :partida ORDER BY r.numeroDeRonda DESC";
        List<Ronda> rondas = sessionFactory.getCurrentSession()
                .createQuery(hql, Ronda.class)
                .setParameter("partida", partida)
                .setMaxResults(1)
                .list();

        return rondas.isEmpty() ? null : rondas.get(0);
    }

    @Override
    public List<Ronda> buscarTodasLasRondas() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Ronda ORDER BY fechaHora DESC", Ronda.class)
                .list();
    }

    @Override
    public Long contarRondasDePartida(Partida2 partida) {
        String hql = "SELECT COUNT(r) FROM Ronda r WHERE r.partida = :partida";
        return (Long) sessionFactory.getCurrentSession()
                .createQuery(hql)
                .setParameter("partida", partida)
                .uniqueResult();
    }

    @Override
    public void eliminar(Ronda ronda) {
        sessionFactory.getCurrentSession().delete(ronda);
    }

    @Override
    public void actualizar(Ronda ronda) {
        sessionFactory.getCurrentSession().update(ronda);
    }

    @Override
    public Ronda buscarRondaActivaPorPartidaId(Long id) {
        return null;
    }
}