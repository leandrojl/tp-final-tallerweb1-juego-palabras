package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Usuario;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RepositorioUsuarioImplTest {

    private SessionFactory sessionFactoryMock;
    private Session sessionMock;
    private RepositorioUsuarioImpl repositorioUsuario;
    private Criteria criteriaMock;

    @BeforeEach
    public void setUp() {
        this.sessionMock = Mockito.mock(Session.class);
        this.sessionFactoryMock = Mockito.mock(SessionFactory.class);
        this.repositorioUsuario = new RepositorioUsuarioImpl(sessionFactoryMock);
        this.criteriaMock = Mockito.mock(Criteria.class);
    }


    @Test
    public void queSePuedaGuardarUnUsuario(){
        Usuario usuario = givenCrearUsuario("pepe123","pepe123@gmail.com","password");
        Serializable id = 1L;
        Usuario usuario2 = givenCrearUsuario("lucas456","lucas456@gmail.com","password2");
        Serializable id2 = 2L;
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        when(sessionFactoryMock.getCurrentSession().save(usuario)).thenReturn(id);
        when(sessionFactoryMock.getCurrentSession().save(usuario2)).thenReturn(id2);

        Serializable idQueLlega = whenGuardarUsuarioEnBdd(usuario);
        Serializable idQueLlega2 = whenGuardarUsuarioEnBdd(usuario2);
        thenUsuarioGuardado(id,idQueLlega);
        thenUsuarioGuardado(id2,idQueLlega2);
    }


    @Test
    public void queSePuedaBuscarUnUsuario(){
        Usuario usuarioEsperado = givenExisteUsuario();
        when(sessionMock.createCriteria(Usuario.class)).thenReturn(criteriaMock);
        when(criteriaMock.add(any(Criterion.class))).thenReturn(criteriaMock);
        when(criteriaMock.uniqueResult()).thenReturn(usuarioEsperado);
        String nombreBuscado = "pepe123";
        Usuario usuarioObtenido = repositorioUsuario.buscar(nombreBuscado);
        assertEquals(nombreBuscado, usuarioObtenido.getUsuario());
    }

    private void thenUsuarioEncontradoExitosamente(String nombreBuscado, Usuario usuarioEncontrado) {
        assertEquals(nombreBuscado, usuarioEncontrado.getUsuario());
    }

    private Usuario whenBuscarUsuario(String nombreBuscado) {
        return this.repositorioUsuario.buscar(nombreBuscado);
    }

    private Usuario givenExisteUsuario() {
        Usuario usuario = givenCrearUsuario("pepe123","pepe123@gmail.com","password");
        Serializable id = 1L;
        when(sessionFactoryMock.getCurrentSession()).thenReturn(sessionMock);
        when(sessionFactoryMock.getCurrentSession().save(usuario)).thenReturn(id);
        whenGuardarUsuarioEnBdd(usuario);
        return usuario;
    }


    private static Usuario givenCrearUsuario(String nombre, String email, String password) {
        return new Usuario(nombre,email ,password );
    }

    private void thenUsuarioGuardado(Serializable id, Serializable idQueLlega) {
        assertSame(id, idQueLlega);
    }

    private Serializable whenGuardarUsuarioEnBdd(Usuario usuario) {
        return this.repositorioUsuario.guardar(usuario);
    }
}
