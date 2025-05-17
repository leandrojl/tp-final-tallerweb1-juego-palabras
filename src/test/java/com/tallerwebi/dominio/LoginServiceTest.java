package com.tallerwebi.dominio;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class LoginServiceTest {

    private LoginService loginService = new LoginServiceImpl();

    @Test
    public void queSePuedaIniciarSesion(){
        givenExisteUsuario();
        Usuario usuario = whenLogin("pepe1235421","abc123245");
        thenLoginExitoso(usuario);
    }

    @Test
    public void queAlBuscarUnUsuarioObtengaLoPropio(){
        givenExisteUsuario();
        Usuario usuario = whenBuscarUsuario("pepe1235421");
        thenUsuarioEncontrado(usuario,"pepe1235421");
    }


    @Test
    public void siElNombreDeUsuarioNoExisteElLoginFalla(){
        Usuario usuario = whenLogin("random","abc123245");
        thenLoginFalla(usuario);
    }


    @Test
    public void siLaPasswordEsIncorrectaElLoginFalla(){
        Usuario usuario = whenLogin("pepe1235421","passwordIncorrecta");
        thenLoginFalla(usuario);
    }





    private void thenLoginFalla(Usuario usuario) {
        assertThat(usuario, is(nullValue()));
    }

    private void thenUsuarioEncontrado(Usuario usuario,String nombre) {
        assertThat(usuario.getNombre(), is(nombre));
    }

    private Usuario whenBuscarUsuario(String nombre) {
        return loginService.buscarUsuario(nombre);
    }


    private void thenLoginExitoso(Usuario usuario) {
        assertThat(usuario, is(notNullValue()));
    }

    private Usuario whenLogin(String nombre, String password) {
        return loginService.login(nombre,password);
    }

    private void givenExisteUsuario() {
    }
}
