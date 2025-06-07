package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class RepositorioUsuarioTest {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private RepositorioUsuarioImpl repositorioUsuario;

    @Test
    @Transactional
    @Rollback
    public void queSePuedaGuardarUnUsuario(){
        Usuario usuario = givenCrearUsuario("pepe123","pepe123@gmail.com","123456");
        Serializable id = whenGuardarUsuarioEnBdd(usuario);
        thenUsuarioGuardado(id);
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnUsuario(){
        givenExisteUsuario();
        Usuario usuarioEncontrado = whenBuscarUsuario("pepe123");
        thenUsuarioEncontradoExitosamente("pepe123",usuarioEncontrado);
    }

    private void thenUsuarioEncontradoExitosamente(String nombreBuscado, Usuario usuarioEncontrado) {
        assertEquals(nombreBuscado, usuarioEncontrado.getUsuario());
    }

    private Usuario whenBuscarUsuario(String nombreBuscado) {
        return this.repositorioUsuario.buscar(nombreBuscado);
    }

    private void givenExisteUsuario() {
        Usuario usuario = givenCrearUsuario("pepe123","pepe123@gmail.com","123456");
        this.sessionFactory.getCurrentSession().save(usuario);
    }


    private static Usuario givenCrearUsuario(String usuario, String email, String password) {
        return new Usuario(usuario,email ,password );
    }

    private void thenUsuarioGuardado(Serializable id) {
        assertNotNull(id);
    }

    private Serializable whenGuardarUsuarioEnBdd(Usuario usuario) {
        return this.repositorioUsuario.guardar(usuario);
    }
}
