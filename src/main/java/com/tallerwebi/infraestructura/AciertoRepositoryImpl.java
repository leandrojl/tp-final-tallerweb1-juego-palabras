package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.interfaceRepository.AciertoRepository;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.tallerwebi.dominio.model.Acierto;

@Repository
public class AciertoRepositoryImpl implements AciertoRepository {

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


    @Override
    public boolean jugadorYaAcerto(Long usuarioId, Long rondaId) {
        String hql = "SELECT count(a) FROM Acierto a WHERE a.usuario.id = :usuarioId AND a.ronda.id = :rondaId";

        Long cantidad = sessionFactory.getCurrentSession()
                .createQuery(hql, Long.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("rondaId", rondaId)
                .uniqueResult();

        return cantidad != null && cantidad > 0;
    }

    @Override
    public void registrarAcierto(Acierto acierto) {
        sessionFactory.getCurrentSession().save(acierto);
    }

    @Override
    public int contarAciertosPorRondaId(Long rondaId) {
        Long cantidad = sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(a) FROM Acierto a WHERE a.ronda.id = :rondaId", Long.class)
                .setParameter("rondaId", rondaId)
                .uniqueResult();

        return cantidad != null ? cantidad.intValue() : 0;
    }

}

