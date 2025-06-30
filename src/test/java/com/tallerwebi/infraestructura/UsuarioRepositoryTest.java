package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SimpMessagingMockConfigTest;
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
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {
        SpringWebTestConfig.class,
        HibernateTestConfig.class,
        SimpMessagingMockConfigTest.class
})
public class UsuarioRepositoryTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private UsuarioRepositoryImpl repositorioUsuario;

    @Test
    @Transactional
    @Rollback
    public void queSePuedaGuardarUnUsuario() {
        Usuario usuario = crearUsuario("pepe123", "pepe@gmail.com", "123456");
        Serializable id = repositorioUsuario.guardar(usuario);
        assertNotNull(id);
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnUsuarioPorNombre() {
        Usuario usuario = crearYGuardarUsuario("pepe123", "pepe@gmail.com", "123456");
        Usuario encontrado = repositorioUsuario.buscar("pepe123");
        assertNotNull(encontrado);
        assertEquals("pepe123", encontrado.getNombreUsuario());
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnUsuarioPorId() {
        Usuario usuario = crearYGuardarUsuario("pepe123", "pepe@gmail.com", "123456");
        Usuario encontrado = repositorioUsuario.buscarPorId(usuario.getId());
        assertNotNull(encontrado);
        assertEquals(usuario.getId(), encontrado.getId());
    }



    @Test
    @Transactional
    @Rollback
    public void queSePuedaModificarUnUsuario() {
        Usuario usuario = crearYGuardarUsuario("pepe123", "pepe@gmail.com", "123456");

        usuario.setPassword("nuevaClave123");
        repositorioUsuario.modificar(usuario);

        Usuario actualizado = repositorioUsuario.buscarPorId(usuario.getId());
        assertEquals("nuevaClave123", actualizado.getPassword());
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedanObtenerTodosLosUsuarios() {
        crearYGuardarUsuario("pepe1", "1@a.com", "pass1");
        crearYGuardarUsuario("pepe2", "2@a.com", "pass2");

        List<Usuario> usuarios = repositorioUsuario.obtenerTodosLosUsuariosLogueados();
        assertFalse(usuarios.isEmpty());
        assertTrue(usuarios.size() >= 2);
    }

    // --- Métodos auxiliares ---
    private Usuario crearUsuario(String nombre, String mail, String clave) {
        return new Usuario(nombre, mail, clave);
    }

    private Usuario crearYGuardarUsuario(String nombre, String mail, String clave) {
        Usuario usuario = crearUsuario(nombre, mail, clave);
        sessionFactory.getCurrentSession().save(usuario);
        return usuario;
    }
}

