// src/test/java/com/tallerwebi/presentacion/PerfilControllerTest.java
package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.PerfilService;
import com.tallerwebi.dominio.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpSession;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.web.servlet.ModelAndView;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.mockito.Mockito.*;

public class PerfilControllerTest {

    private PerfilService perfilServiceMock;
    private PerfilController perfilControllerMock;
    private HttpSession sessionMock;

    @BeforeEach
    void setUp() {
        perfilServiceMock = mock(PerfilService.class);
        perfilControllerMock = new PerfilController(perfilServiceMock);
        sessionMock = mock(HttpSession.class);
    }

    // 1. El controlador se instancia correctamente
    @Test
    void sePuedeInstanciarElControlador() {
        assertNotNull(perfilControllerMock);
    }

    // 2. Llama al servicio con el ID correcto
    @Test
    void llamaAlServicioConElIdDeSesion() {
        Long usuarioId = 42L;
        when(sessionMock.getAttribute("usuarioId")).thenReturn(usuarioId);
        Usuario usuario = new Usuario();
        when(perfilServiceMock.obtenerDatosDelPerfilPorId(usuarioId)).thenReturn(usuario);

        perfilControllerMock.irAPerfil(sessionMock);

        verify(perfilServiceMock).obtenerDatosDelPerfilPorId(usuarioId);
    }

    @Test
    void elModeloContieneElUsuario() {
        Long usuarioId = 7L;
        when(sessionMock.getAttribute("usuarioId")).thenReturn(usuarioId);
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("test");
        when(perfilServiceMock.obtenerDatosDelPerfilPorId(usuarioId)).thenReturn(usuario);

        ModelAndView mav = perfilControllerMock.irAPerfil(sessionMock);

        assertEquals(usuario, mav.getModel().get("usuario"));
    }

    @Test
    void retornaLaVistaPerfil() {
        Long usuarioId = 1L;
        when(sessionMock.getAttribute("usuarioId")).thenReturn(usuarioId);
        Usuario usuario = new Usuario();
        when(perfilServiceMock.obtenerDatosDelPerfilPorId(usuarioId)).thenReturn(usuario);

        ModelAndView mav = perfilControllerMock.irAPerfil(sessionMock);

        assertEquals("perfil", mav.getViewName());
    }
}