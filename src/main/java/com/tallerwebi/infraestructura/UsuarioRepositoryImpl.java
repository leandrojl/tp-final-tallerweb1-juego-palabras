package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.model.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository("repositorioUsuario")
public class UsuarioRepositoryImpl implements UsuarioRepository{


    private SessionFactory sessionFactory ;

    public UsuarioRepositoryImpl(){}
    @Autowired
    public UsuarioRepositoryImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarUsuario(String usuario, String password) {

        final Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createCriteria(Usuario.class)
                .add(Restrictions.eq("usuario", usuario))
                .add(Restrictions.eq("password", password))
                .uniqueResult();
    }

    @Override
    public Serializable guardar(Usuario usuario) {

        return sessionFactory.getCurrentSession().save(usuario);
    }

    @Override
    public Usuario buscar(String nombreUsuario) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("nombreUsuario", nombreUsuario))
                .uniqueResult();
    }

    @Override
    public void modificar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

    @Override
    public Usuario buscarPorId(Long i) {
               return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class).add(Restrictions.eq("id", i)).uniqueResult();
    }

    @Override
    public List<Usuario> obtenerTodosLosUsuariosLogueados() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Usuario u ", Usuario.class)
                .getResultList();
    }

    @Override
    public String obtenerNombrePorId(Long usuarioId) {

        Usuario usuario = buscarPorId(usuarioId);
        if (usuario != null) {
            return usuario.getNombreUsuario();
        }
        return null;
    }

    @Override
    public Usuario obtenerUsuarioPorNombre(String nombreUsuario) {
        Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario")
                .setParameter("nombreUsuario", nombreUsuario)
                .uniqueResult();
    }

    @Override
    public void actualizarEstado(Long idUsuario, boolean estado) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE Usuario u SET u.estaListo = :estado WHERE u.id = :id")
                .setParameter("estado", estado)
                .setParameter("id", idUsuario)
                .executeUpdate();
        session.flush();
    }

    @Override
    public String obtenerEmailPorId(long idUsuario) {
        Session session = sessionFactory.getCurrentSession();

        return (String) session.createQuery("SELECT u.email FROM Usuario u WHERE u.id = :idUsuario")
                .setParameter("idUsuario", idUsuario)
                .uniqueResult();
    }

    @Override
    public void agregarMonedasAlUsuario(JSONObject respuesta) {
        Session session = sessionFactory.getCurrentSession();

        JSONObject metadata = respuesta.getJSONObject("metadata");
        int monedasCompradas = metadata.getInt("monedas");
        long usuarioId = metadata.getLong("user_id");
        session.createQuery("UPDATE Usuario SET moneda = moneda + :monedasCompradas WHERE id = :usuarioId")
                .setParameter("monedasCompradas", monedasCompradas)
                .setParameter("usuarioId", usuarioId)
                .executeUpdate();
    }

    @Override
    public void restarMonedas(int valorComodinLetra, Long idUsuario) {
        Session session = sessionFactory.getCurrentSession();
        session.createQuery("UPDATE Usuario SET moneda = moneda - :monedasCompradas WHERE id = :usuarioId")
                .setParameter("monedasCompradas", valorComodinLetra)
                .setParameter("usuarioId", idUsuario)
                .executeUpdate();
    }


}