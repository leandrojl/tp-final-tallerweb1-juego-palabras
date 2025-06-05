package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import com.tallerwebi.dominio.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThrows(DatosLoginIncorrectosException.class, ()->whenLogin("random","abc123245"));
    }


    @Test
    public void siLaPasswordEsIncorrectaElLoginFalla(){
        assertThrows(DatosLoginIncorrectosException.class, ()->whenLogin("pepe1235421","passwordIncorrecta"));
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
