package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioPartidaRepositoryImpl implements UsuarioPartidaRepository{

    private final SessionFactory sessionFactory;

    @Autowired
    public UsuarioPartidaRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Partida2 partida) {
        sessionFactory.getCurrentSession().save(partida);
    }

    @Override
    public void borrar(Partida2 partida) {
        sessionFactory.getCurrentSession().delete(partida);
    }

    @Override
    public UsuarioPartida buscarPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "FROM UsuarioPartida up WHERE up.usuario.id = :usuarioId AND up.partida.id = :partidaId",
                        UsuarioPartida.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("partidaId", partidaId)
                .uniqueResult();
    }

    @Override
    public List<UsuarioPartida> buscarPorPartidaId(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "FROM UsuarioPartida up WHERE up.partida.id = :partidaId",
                        UsuarioPartida.class)
                .setParameter("partidaId", id)
                .list();
    }
}
