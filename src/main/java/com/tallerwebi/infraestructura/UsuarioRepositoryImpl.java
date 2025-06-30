package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
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

}