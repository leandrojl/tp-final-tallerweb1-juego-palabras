package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.interfaceRepository.Partida2Repository;
import com.tallerwebi.dominio.model.Partida2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Repository
public class Partida2RepositoryImpl implements Partida2Repository {

    private final SessionFactory sessionFactory;

    @Autowired
    public Partida2RepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    @Override
    public Serializable crearPartida(Partida2 nuevaPartida) {
        return sessionFactory.getCurrentSession().save(nuevaPartida);

    }
}