package com.tallerwebi.dominio;


import com.tallerwebi.dominio.model.Palabra;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PalabraRepositoryImpl implements PalabraRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PalabraRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Palabra palabra) {
        sessionFactory.getCurrentSession().save(palabra);
    }


    @Override
    public List<Palabra> buscarPorIdioma(String idioma) {
        String hql = "FROM Palabra p WHERE p.idioma = :idioma";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Palabra.class)
                .setParameter("idioma", idioma)
                .list();
    }

    @Override
    public List<Palabra> buscarTodas() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Palabra", Palabra.class)
                .list();
    }

    @Override
    public Long contar() {
        return (Long) sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(p) FROM Palabra p")
                .uniqueResult();
    }
}
