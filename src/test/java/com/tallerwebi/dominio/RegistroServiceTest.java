package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.PasswordMenorAOchoCaracteresException;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteException;
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
public class RegistroServiceTest {
    private RepositorioUsuario repositorioUsuario;
    private RegistroService registroService;
    private String nombreUsuario = "pepe123";

    @BeforeEach
    public void init(){
        this.repositorioUsuario = Mockito.mock(RepositorioUsuario.class);
        this.registroService = new RegistroServiceImpl(repositorioUsuario);
    }

    @Test
    public void queSePuedaRealizarUnRegistro() {
        givenNoExisteUsuario();
        Usuario usuario = whenRegistroUsuario("pepe123446","contrasenia123");
        thenRegistroExitoso(usuario);
    }


    @Test
    public void queAlBuscarUnUsuarioObtengaLoPropio(){
        givenExisteUsuario();
        Usuario usuario = whenBuscarUsuario(nombreUsuario);
        thenUsuarioEncontrado(usuario,nombreUsuario);
    }


    @Test
    public void siAlRegistrarseYaExisteElNombreELRegistroFalla(){
        givenExisteUsuario();
        assertThrows(UsuarioExistenteException.class,() -> whenRegistroUsuario(nombreUsuario,"contra1ds1f32s"));
    }

    @Test
    public void siLaPasswordTieneMenosDeOchoCaracteresElRegistroFalla(){
        givenNoExisteUsuario();
        assertThrows(PasswordMenorAOchoCaracteresException.class,() -> whenRegistroUsuario("otropepe","contra1"));
    }







    private void thenUsuarioEncontrado(Usuario usuario,String nombre) {
        assertThat(usuario.getUsuario(), is(nombre));
    }

    private Usuario whenBuscarUsuario(String nombre) {
        return registroService.buscarUsuario(nombre);
    }
    private void givenExisteUsuario() {
        Usuario esperado = new Usuario(nombreUsuario, "pepe@gmail.com", "abc123245");
        when(repositorioUsuario.buscar(nombreUsuario)).thenReturn(esperado);
    }

    private void givenNoExisteUsuario() {
        when(this.repositorioUsuario.buscar("pepe123446")).thenReturn(null);
    }

    private void thenRegistroExitoso(Usuario usuario) {
        assertThat(usuario,is(notNullValue()));
    }

    private Usuario whenRegistroUsuario(String usuario, String password) {
        return registroService.registrar(usuario,password);
    }
}
