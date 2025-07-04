package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.interfaceRepository.RondaRepository;
import com.tallerwebi.dominio.model.Ronda;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RondaRepositoryImpl implements RondaRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void guardar(Ronda ronda) {
        sessionFactory.getCurrentSession().save(ronda);
    }

    @Override
    public List<Ronda> buscarPorPartidaConPalabra(Long partidaId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT r FROM Ronda r JOIN FETCH r.palabra WHERE r.partida.id = :partidaId", Ronda.class)
                .setParameter("partidaId", partidaId)
                .getResultList();
    }



    @Override
    public int obtenerCantidadDeRondasPorPartida(Long partidaId) {
        Long count = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(r) FROM Ronda r WHERE r.partida.id = :partidaId", Long.class)
                .setParameter("partidaId", partidaId)
                .uniqueResult();
        return count.intValue();
    }


    @Override
    public Ronda obtenerUltimaRondaDePartida(Long partidaId) {

            return sessionFactory.getCurrentSession()
                    .createQuery(
                            "SELECT r FROM Ronda r " +
                                    "JOIN FETCH r.palabra p " +
                                    "LEFT JOIN FETCH p.definiciones " +
                                    "WHERE r.partida.id = :partidaId " +
                                    "ORDER BY r.numeroDeRonda DESC", Ronda.class)
                    .setParameter("partidaId", partidaId)
                    .setMaxResults(1)
                    .setLockMode("r", LockMode.PESSIMISTIC_WRITE)
                    .uniqueResult();

    }

    @Override
    public void actualizar(Ronda ronda) {
        sessionFactory.getCurrentSession().update(ronda);
    }


    public Ronda buscarPorId(Long rondaId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Ronda r WHERE r.id = :rondaId", Ronda.class)
                .setParameter("rondaId", rondaId)
                .uniqueResult();
    }

}
