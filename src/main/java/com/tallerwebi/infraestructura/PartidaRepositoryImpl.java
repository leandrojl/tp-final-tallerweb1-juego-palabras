package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.dominio.model.Partida;
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
    public Partida buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Partida.class, id);
    }

    // Ejemplo: guardar una partida (por si querés usarlo después)
    @Override
    public void guardar(Partida partida) {
        sessionFactory.getCurrentSession().save(partida);
    }

    @Override
    public Serializable crearPartida(Partida nuevaPartida) {
        return sessionFactory.getCurrentSession().save(nuevaPartida);
    }

    @Override
    public void actualizarEstado(Long idPartida, Estado estado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE Partida p SET p.estado = :estado WHERE p.id = :idPartida")
                .setParameter("idPartida", idPartida)
                .setParameter("estado",estado)
                .executeUpdate();

    }
}
