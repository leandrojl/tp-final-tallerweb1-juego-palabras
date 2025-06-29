package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsuarioPartidaRepositoryImpl implements UsuarioPartidaRepository {

    SessionFactory sessionFactory;
    public UsuarioPartidaRepositoryImpl(){}
    @Autowired
    public UsuarioPartidaRepositoryImpl(SessionFactory sessionFactory){
    this.sessionFactory = sessionFactory;}


    @Override
    public int getCantidadDePartidasDeJugador(Usuario usuario) {

         Long resultado = (long) sessionFactory.getCurrentSession().createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario",usuario))
                .setProjection(Projections.rowCount())
                .uniqueResult();
         int cantidad = (resultado!=null)? resultado.intValue() : 0;
        return cantidad;
    }

    @Override
    public int getCantidadDePartidasGanadasDeJugador(Usuario usuario) {
        Long resultado = (long) sessionFactory.getCurrentSession().createCriteria(UsuarioPartida.class)
                .add(Restrictions.eq("usuario",usuario))
                .add(Restrictions.eq("gano",true))
                .setProjection(Projections.rowCount())
                .uniqueResult();
        int cantidad = (resultado!=null)? resultado.intValue() : 0;
        return cantidad;


    }

    @Override
    public double getWinrate(Usuario usuario) {
        int partidasJugadas=getCantidadDePartidasDeJugador(usuario);
        int partidasGanadas=getCantidadDePartidasGanadasDeJugador(usuario);

        return  partidasJugadas>0 ?partidasGanadas*100.0/partidasJugadas : 0.0;
    }

    @Override
    public List<Object[]> obtenerRanking() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "SELECT u.nombreUsuario, COUNT(p) " +
                        "FROM UsuarioPartida p " +
                        "JOIN p.usuario u " +
                        "WHERE p.gano = true " +
                        "GROUP BY u.nombreUsuario " +
                        "ORDER BY COUNT(p) DESC", Object[].class
        ).getResultList();
    }

    @Override
    public List<Usuario> obtenerUsuariosDeUnaPartida(Long idPartida) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.createQuery(
                        "SELECT up.usuario FROM UsuarioPartida up WHERE up.partida.id = :idPartida",
                        Usuario.class
                ).setParameter("idPartida", idPartida)
                .getResultList();
    }

    @Override
    public UsuarioPartida obtenerUsuarioPartida(Usuario usuario, Partida2 partida) {
        Session session = this.sessionFactory.getCurrentSession();

        return session.createQuery(
                        "FROM UsuarioPartida up WHERE up.usuario = :usuario AND up.partida = :partida",
                        UsuarioPartida.class
                )
                .setParameter("usuario", usuario)
                .setParameter("partida", partida)
                .uniqueResult();
    }

    @Override
    public void borrarUsuarioPartidaAsociadaAlUsuario(Long idPartida, Long idUsuario) {
        Session session = this.sessionFactory.getCurrentSession();
        session.createQuery("DELETE FROM UsuarioPartida u " +
                "WHERE u.partida = :idPartida AND u.usuario = :idUsuario")
                .setParameter("idPartida", idPartida)
                .setParameter("idUsuario", idUsuario);
    }

    @Override
    public Partida2 obtenerPartida(Long idPartida) {
        Session session = this.sessionFactory.getCurrentSession();
        return (Partida2) session.createQuery(
                        "SELECT p FROM UsuarioPartida up JOIN up.partida p WHERE p.id = :idPartida"
                )
                .setParameter("idPartida", idPartida)
                .uniqueResult();
    }


}
