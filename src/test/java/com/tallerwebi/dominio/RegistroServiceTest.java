package com.tallerwebi.dominio;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RegistroServiceTest {

    private RegistroService registroService = new RegistroServiceImpl();

    @Test
    public void queSePuedaRealizarUnRegistro() {
        givenNoExisteUsuario();
        Usuario usuario = whenRegistroUsuario("pepe123446","contrasenia123");
        thenRegistroExitoso(usuario);
    }


    @Test
    public void queAlBuscarUnUsuarioObtengaLoPropio(){
        givenExisteUsuario();
        Usuario usuario = whenBuscarUsuario("pepe1235421");
        thenUsuarioEncontrado(usuario,"pepe1235421");
    }


    @Test
    public void siAlRegistrarseYaExisteElNombreELRegistroFalla(){
        assertThrows(UsuarioExistenteException.class,() -> whenRegistroUsuario("pepe1235421","contra1ds1f32s"));
    }

    @Test
    public void siLaPasswordTieneMenosDeOchoCaracteresElRegistroFalla(){
        givenNoExisteUsuario();
        assertThrows(PasswordMenorAOchoCaracteresException.class,() -> whenRegistroUsuario("otropepe","contra1"));
    }







    private void thenUsuarioEncontrado(Usuario usuario,String nombre) {
        assertThat(usuario.getNombre(), is(nombre));
    }

    private Usuario whenBuscarUsuario(String nombre) {
        return registroService.buscarUsuario(nombre);
    }
    private void givenExisteUsuario() {
    }

    private void thenRegistroFalla(Usuario usuario) {
        assertThat(usuario, is(nullValue()));
    }
    private void givenNoExisteUsuario() {
    }

    private void thenRegistroExitoso(Usuario usuario) {
        assertThat(usuario,is(notNullValue()));
    }

    private Usuario whenRegistroUsuario(String nombre, String password) {
        return registroService.registrar(nombre,password);
    }
}
