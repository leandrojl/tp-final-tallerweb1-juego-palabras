package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.interfaceRepository.LobbyRepository;
import com.tallerwebi.dominio.Enum.Estado;
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
    public void guardar(Partida partida) {
        Session session = sessionFactory.getCurrentSession();
        session.save(partida);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Partida> obtenerPartidasEnEspera() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Partida p WHERE p.estado = :estado", Partida.class)
                .setParameter("estado", Estado.EN_ESPERA)
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Partida> obtenerPartidasEnCurso() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Partida p WHERE p.estado = :estado", Partida.class)
                .setParameter("estado", Estado.EN_CURSO)
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Partida> obtenerPartidasFinalizadas() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Partida p WHERE p.estado = :estado", Partida.class)
                .setParameter("estado", Estado.FINALIZADA)
                .getResultList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Partida> obtenerPartidasCanceladas() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Partida p WHERE p.estado = :estado", Partida.class)
                .setParameter("estado", Estado.CANCELADA)
                .getResultList();
    }
    @Transactional(readOnly = true)
    @Override
    public List<Partida> obtenerPartidasPorNombre(String nombre) {
    Session session = sessionFactory.getCurrentSession();
    nombre="%"+nombre+"%";
    System.out.println(nombre);
    return session.createQuery("FROM Partida p WHERE p.estado = :estado AND p.nombre LIKE :nombre", Partida.class)
                .setParameter("estado", Estado.EN_ESPERA)
                .setParameter("nombre", nombre)
                .getResultList();


    }


    @Transactional
    @Override
    public void eliminarTodasLasPartidas() {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("DELETE FROM Partida").executeUpdate();
    }


}