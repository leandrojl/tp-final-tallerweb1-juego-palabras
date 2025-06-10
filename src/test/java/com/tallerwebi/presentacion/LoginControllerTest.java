package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.excepcion.DatosLoginIncorrectosException;
import com.tallerwebi.dominio.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Transactional
@Rollback
public class LoginControllerTest {

    private LoginController loginController;
    private String nombreUsuario = "lucas";
    private String password = "12151gdsf";
    private MockMvc mockMvc;
    private LoginService loginService;
    //private HttpSession session;
    //private HttpServletRequest httpServletRequest;

    @BeforeEach
    public void init(){
        this.loginService = Mockito.mock(LoginService.class);
        this.loginController = new LoginController(loginService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        //this.session = Mockito.mock(HttpSession.class);
        //this.httpServletRequest = Mockito.mock(HttpServletRequest.class);
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
        MvcResult mvcResult = whenLoguearse("",password);
        thenLoginFalla(mvcResult,"El campo de usuario no puede estar vacio");
    }


    @Test
    public void siElCampoPasswordEstaVacioFallaElLogin() throws Exception {
        MvcResult mvcResult = whenLoguearse(nombreUsuario,"");

        thenLoginFalla(mvcResult,"El campo de contraseña no puede estar vacio");
    }

    @Test
    public void siAlgunCampoEsIncorrectoElLoginFalla() throws Exception {
        when(loginService.login("saraza","123132")).thenThrow(new DatosLoginIncorrectosException());
    }

    @Test
    public void siLosDatosDeLoginSonCorrectosSeRedireccionaAlLobby() throws Exception {
        MvcResult mvcResult = whenLoguearse(nombreUsuario,password);

        thenLoginExitoso(mvcResult,"lobby");
    }

    @Test
    public void siLosDatosDeLoginSonCorrectosSeGuardaElUsuarioEnSesion() throws Exception {
        Usuario usuarioLogueado = new Usuario(nombreUsuario, "lucas@gmail.com", password);
        when(loginService.login(nombreUsuario, password)).thenReturn(usuarioLogueado);
        MvcResult mvcResult = whenLoguearse(nombreUsuario,password);


        HttpSession httpSession = mvcResult.getRequest().getSession(false);

        assertNotNull(httpSession);
        Usuario usuarioEsperado = (Usuario) httpSession.getAttribute("usuario");
        assertEquals(nombreUsuario,usuarioEsperado.getNombreUsuario());
    }


    private void thenLoginExitoso(MvcResult mvcResult, String vista) {
        ModelAndView mav = mvcResult.getModelAndView();
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

    private void thenLoginFalla(MvcResult mvcResult, String mensaje) {
        ModelAndView mav = mvcResult.getModelAndView();
        assertThat(mav.getModel().get("error").toString(), equalToIgnoringCase(mensaje));
    }

    private MvcResult whenLoguearse(String nombreUsuario, String password) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/procesarLogin")
                .param("nombreUsuario",nombreUsuario)
                .param("password",password))
                .andExpect(status().isOk())
                .andReturn();
        return mvcResult;
    }
}
