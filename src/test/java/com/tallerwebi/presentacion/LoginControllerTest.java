package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import com.tallerwebi.dominio.interfaceService.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Transactional
@Rollback
public class LoginControllerTest {

    private LoginController loginController;
    private String nombreUsuario = "lucas";
    private String password = "12151gdsf";
    private MockMvc mockMvc;
    private LoginService loginService;
    private HttpSession session;

    @BeforeEach
    public void init(){
        this.loginService = mock(LoginService.class);
        this.loginController = new LoginController(loginService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        session = mock(HttpSession.class);
    }

    @Test
    public void queAlCrearObjetoLoginControllerNoSeaNulo(){
        assertNotNull(loginController);
    }

    @Test
    public void queSePuedaMostrarLaVistaLogin(){
        ModelAndView mav = whenMostrarVistaLogin();

        thenViajaAVistaIndicada(mav,"login");
    }

    @Test
    public void queSePuedaRedireccionarALaVistaRegistro(){

        ModelAndView mav = whenRedireccionarAVistaRegistro();

        thenViajaAVistaIndicada(mav,"registro");
    }

    @Test
    public void siElCampoUsuarioEstaVacioFallaElLogin() throws Exception {
        when(loginService.login("",password)).thenThrow(DatosLoginIncorrectosException.class);
        ModelAndView mav = loginController.login(new Usuario("","123132"),session);
        thenLoginFalla(mav,"El campo de usuario no puede estar vacio");
    }


    @Test
    public void siElCampoPasswordEstaVacioFallaElLogin() throws Exception {
        when(loginService.login(nombreUsuario,"")).thenThrow(DatosLoginIncorrectosException.class);
        ModelAndView mav = loginController.login(new Usuario(nombreUsuario,""),session);
        thenLoginFalla(mav,"El campo de contraseña no puede estar vacio");
    }

    @Test
    public void siAlgunCampoEsIncorrectoElLoginFalla() {
        DatosLoginIncorrectosException datosLoginIncorrectos = new DatosLoginIncorrectosException("Hubo un error al iniciar sesion");
        when(loginService.login("saraza","123132")).thenThrow(datosLoginIncorrectos);
        ModelAndView mav = loginController.login(new Usuario("saraza","123132"),session);

        assertEquals(mav.getModel().get("error"),datosLoginIncorrectos.getMessage());
    }

    @Test
    public void siLosDatosDeLoginSonCorrectosSeRedireccionaAlLobby() {
        Usuario usuario = new Usuario(nombreUsuario,password);
        when(loginService.login(nombreUsuario,password)).thenReturn(usuario);
        ModelAndView mav = loginController.login(usuario,session);

        thenLoginExitoso(mav,"redirect:/lobby");
    }


    private void thenLoginExitoso(ModelAndView mav, String vista) {
        assertThat(mav.getViewName(),equalToIgnoringCase(vista));
    }


    private void thenViajaAVistaIndicada(ModelAndView mav, String vista) {
        assertThat(mav.getViewName(),equalToIgnoringCase(vista));
    }

    private ModelAndView whenMostrarVistaLogin() {
        return this.loginController.mostrarLogin();
    }

    private ModelAndView whenRedireccionarAVistaRegistro() {
        return this.loginController.redireccionarAVistaRegistro();
    }

    private void thenLoginFalla(ModelAndView mav, String mensaje) {
        assertEquals(mav.getModel().get("error").toString(), mensaje);
    }
}
