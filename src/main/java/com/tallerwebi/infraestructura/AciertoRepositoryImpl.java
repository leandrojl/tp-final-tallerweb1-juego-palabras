package com.tallerwebi.infraestructura;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public class AciertoRepositoryImpl implements AciertoRepository{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public long contarUsuariosQueAcertaron(Long idRonda) {
        String hql = "SELECT COUNT(DISTINCT a.usuario) FROM Acierto a WHERE a.ronda.id = :idRonda";
        return sessionFactory.getCurrentSession()
                .createQuery(hql, Long.class)
                .setParameter("idRonda", idRonda)
                .getSingleResult();
    }

}
