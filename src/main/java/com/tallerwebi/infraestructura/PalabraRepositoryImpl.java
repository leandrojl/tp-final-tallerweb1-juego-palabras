package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Palabra;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PalabraRepositoryImpl implements PalabraRepository {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Palabra> buscarPorIdioma(String idioma) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Palabra p WHERE p.idioma = :idioma", Palabra.class)
                .setParameter("idioma", idioma)
                .getResultList();
    }

    @Override
    public List<Palabra> buscarTodas() {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Palabra", Palabra.class)
                .getResultList();
    }

    @Override
    public Palabra buscarPorTexto(String descripcion) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM Palabra p WHERE p.descripcion = :descripcion", Palabra.class)
                .setParameter("descripcion", descripcion)
                .uniqueResult();
    }

    @Override
    public void guardar(Palabra palabra) {
        sessionFactory.getCurrentSession().save(palabra);

    }
}
