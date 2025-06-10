package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.infraestructura.UsuarioRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository("repositorioUsuario")
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UsuarioRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarUsuario(String usuario, String password) {
        String hql = "FROM Usuario u WHERE u.nombreUsuario = :usuario AND u.password = :password";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Usuario.class)
                .setParameter("usuario", usuario)
                .setParameter("password", password)
                .uniqueResult();
    }

    @Override
    public Serializable guardar(Usuario usuario) {
        return (Serializable) sessionFactory.getCurrentSession().save(usuario);
    }

    @Override
    public Usuario buscar(String nombreUsuario) {
        String hql = "FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Usuario.class)
                .setParameter("nombreUsuario", nombreUsuario)
                .uniqueResult();
    }

    @Override
    public void modificar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Usuario.class, id);
    }

    // Opcional: si necesitás una lista de todos los usuarios
    public List<Usuario> buscarTodos() {
        String hql = "FROM Usuario";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Usuario.class)
                .list();
    }
}

