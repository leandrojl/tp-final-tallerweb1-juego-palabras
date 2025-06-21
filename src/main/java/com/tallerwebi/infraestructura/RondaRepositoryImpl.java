package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Ronda;
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
}
