package com.tallerwebi.infraestructura;


import com.tallerwebi.dominio.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.Usuario;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioPartidaRepositoryImpl implements UsuarioPartidaRepository {

    SessionFactory sessionFactory;
    public UsuarioPartidaRepositoryImpl(){}
    @Autowired
    public UsuarioPartidaRepositoryImpl(SessionFactory sessionFactory){
    this.sessionFactory = sessionFactory;}


    @Override
    public int getCantidadDePartidasDeJugador(Usuario usuario) {
        return 0;
    }
}
