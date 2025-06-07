package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import com.tallerwebi.infraestructura.RepositorioUsuarioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
@Transactional
@Rollback
public class LoginServiceTest {

    private LoginService loginService;
    private RepositorioUsuario repositorioUsuario;

    @BeforeEach
    public void setUp() {
        this.repositorioUsuario = Mockito.mock(RepositorioUsuario.class);
        this.loginService = new LoginServiceImpl(repositorioUsuario);
    }

    @Test
    public void queSePuedaIniciarSesion(){
        givenExisteUsuario();
        Usuario usuarioLogueado = whenLogin("pepe1235421","abc123245");
        thenLoginExitoso(usuarioLogueado);
    }


    @Test
    public void queAlBuscarUnUsuarioObtengaLoPropio(){
        givenExisteUsuario();
        Usuario usuario = whenBuscarUsuario("pepe1235421");
        thenUsuarioEncontrado(usuario,"pepe1235421");
    }


    @Test
    public void siElNombreDeUsuarioNoExisteElLoginFalla(){
        givenExisteUsuario();
        assertThrows(DatosLoginIncorrectosException.class, ()->whenLogin("random","abc123245"));
    }


    @Test
    public void siLaPasswordEsIncorrectaElLoginFalla(){
        givenExisteUsuario();
        assertThrows(DatosLoginIncorrectosException.class, ()->whenLogin("pepe1235421","passwordIncorrecta"));
    }



    private void givenExisteUsuario() {
        Usuario esperado = new Usuario("pepe1235421", "pepe@gmail.com", "abc123245");
        when(repositorioUsuario.buscar("pepe1235421")).thenReturn(esperado);
    }

    private void thenUsuarioEncontrado(Usuario usuarioEncontrado,String usuario) {
        assertThat(usuarioEncontrado.getUsuario(), is(usuario));
    }

    private Usuario whenBuscarUsuario(String usuario) {
        return loginService.buscarUsuario(usuario);
    }


    private void thenLoginExitoso(Usuario usuario) {
        assertThat(usuario, is(notNullValue()));
    }

    private Usuario whenLogin(String usuario, String password) {
        return loginService.login(usuario,password);
    }


}
