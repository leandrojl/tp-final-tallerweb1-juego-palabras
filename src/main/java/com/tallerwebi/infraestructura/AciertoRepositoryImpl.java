package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Acierto;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AciertoRepositoryImpl implements AciertoRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public AciertoRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardar(Acierto acierto) {
        sessionFactory.getCurrentSession().save(acierto);
    }
}
