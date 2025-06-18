package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.LobbyRepository;
import com.tallerwebi.dominio.Partida2Repository;
import com.tallerwebi.dominio.model.Partida2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class Partida2RepositoryImpl implements Partida2Repository {

    private final SessionFactory sessionFactory;

    @Autowired
    public Partida2RepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @Override
    public void crearPartida(Partida2 nuevaPartida) {
        Session session = sessionFactory.getCurrentSession();
        session.save(nuevaPartida);
    }
}