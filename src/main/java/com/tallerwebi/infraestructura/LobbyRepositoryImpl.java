package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.LobbyRepository;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class LobbyRepositoryImpl implements LobbyRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public LobbyRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @Override
    public void guardar(Partida2 partida) {
        Session session = sessionFactory.getCurrentSession();
        session.save(partida);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Partida2> obtenerPartidasEnEspera() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Partida2 p WHERE p.estado = :estado", Partida2.class)
                .setParameter("estado", Estado.EN_ESPERA)
                .getResultList();
    }

    @Transactional
    @Override
    public void eliminarTodasLasPartidas() {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("DELETE FROM Partida2").executeUpdate();
    }
}