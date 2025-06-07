package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {


    private final SessionFactory sessionFactory;

    @Autowired
       public RepositorioUsuarioImpl(SessionFactory sessionFactory){
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
    public Usuario buscar(String usuario) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("usuario", usuario))
                .uniqueResult();
    }

    @Override
    public void modificar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

}
