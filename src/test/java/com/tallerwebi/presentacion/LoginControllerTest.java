package com.tallerwebi.presentacion;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    public void init(){
        this.loginController = new LoginController();
    }

    @Test
    public void queAlCrearObjetoLoginControllerNoSeaNulo(){
        assertNotNull(loginController);
    }

    @Test
    public void queSePuedaMostrarLaVistaDeRegistro(){
        ModelAndView mav = whenMostrarVistaLogin();

        thenViajaAVistaIndicada(mav,"login");
    }

    @Test
    public void queSePuedaRedireccionarALaVistaRegistro(){

        ModelAndView mav = whenRedireccionarAVistaRegistro();

        thenViajaAVistaIndicada(mav,"registro");
    }



    @Test
    public void queAlEnviarDatosDeLoginSePuedaVerificarQueElNombreDeUsuarioExiste(){

    }

    @Test
    public void queAlEnviarDatosDeLoginSePuedaVerificarQueElEmailExiste(){

    }

    @Test
    public void queAlEnviarDatosDeLoginSePuedaVerificarQueLaContraseniaEsCorrecta() {

    }

    @Test
    public void siLosDatosDeLoginSonCorrectosSeRedireccioneAlLobby(){

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
}
