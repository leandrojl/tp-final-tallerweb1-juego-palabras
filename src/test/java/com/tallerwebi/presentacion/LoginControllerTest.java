package com.tallerwebi.presentacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LoginControllerTest {

    private LoginController loginController;
    private String usuario = "lucas";
    private String password = "12151gdsf";
    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        this.loginController = new LoginController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
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
        MvcResult mvcResult = whenLoguearse(usuario,"");

        thenLoginFalla(mvcResult,"El campo de contraseña no puede estar vacio");
    }

    @Test
    public void siElUsuarioNoExisteElLoginFalla() throws Exception {
        MvcResult mvcResult = whenLoguearse("random",password);

        thenLoginFalla(mvcResult,"El usuario no existe");
    }

    @Test
    public void siLaContraseniaEsIncorrectaElLoginFalla() throws Exception {
        MvcResult mvcResult = whenLoguearse(usuario,"sfgf12");

        thenLoginFalla(mvcResult,"La contraseña no es correcta");
    }

    @Test
    public void siLosDatosDeLoginSonCorrectosSeRedireccionaAlLobby() throws Exception {
        MvcResult mvcResult = whenLoguearse(usuario,password);

        thenLoginExitoso(mvcResult,"lobby");
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

    private MvcResult whenLoguearse(String usuario, String password) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/procesarLogin")
                .param("usuario",usuario)
                .param("password",password)).andExpect(status().isOk()).andReturn();
        return mvcResult;
    }
}
