package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida2;
import org.hibernate.Session;
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
    public void actualizarEstado(Long idPartida, Estado estado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE Partida2 p SET p.estado = :estado WHERE p.id = :idPartida")
                .setParameter("idPartida", idPartida)
                .setParameter("estado",estado)
                .executeUpdate();
    }
}
