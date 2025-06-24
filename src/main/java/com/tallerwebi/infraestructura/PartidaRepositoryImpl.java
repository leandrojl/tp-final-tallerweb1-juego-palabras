package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Partida2;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public class PartidaRepositoryImpl implements PartidaRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Partida2 buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Partida2.class, id);
    }

    // Ejemplo: guardar una partida (por si querés usarlo después)
    @Override
    public void guardar(Partida2 partida) {
        sessionFactory.getCurrentSession().save(partida);
    }

    @Override
    public Serializable crearPartida(Partida2 nuevaPartida) {
        return sessionFactory.getCurrentSession().save(nuevaPartida);
    }

    @Override
    public void actualizar(Partida2 partida){
        sessionFactory.getCurrentSession().update(partida);
    }
}
