package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.RegistroService;
import com.tallerwebi.dominio.UsuarioExistenteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RegistroControllerTest {

    private RegistroController registroController;
    private ModelAndView mav;
    private String usuario;
    private String password;
    private String email;
    private MockMvc mockMvc;
    private RegistroService registroService;

    @BeforeEach
    public void init(){
        this.registroService = Mockito.mock(RegistroService.class);
        this.registroController = new RegistroController(registroService);
        this.usuario = "pepe123";
        this.password = "abc123";
        this.email = "pepe123@gmail.com";
        this.mockMvc = MockMvcBuilders.standaloneSetup(registroController).build();
    }

    @Test
    public void queAlCrearObjetoRegistroControllerNoSeaNulo(){
        assertNotNull(this.registroController);
    }

    @Test
    public void queSePuedaMostrarLaVistaDeRegistro(){

        ModelAndView mav = whenMostrarVistaRegistro();

        thenViajaAVistaIndicada(mav,"registro");
    }



    @Test
    public void queAlSeleccionarVolverAlLoginSePuedaRedireccionarADichaVista(){

        ModelAndView mav = whenRedireccionarAVistaLogin();

        thenViajaAVistaIndicada(mav,"login");
    }

    @Test
    public void queSePuedaRegistrarUnUsuario() throws Exception {
        givenUsuarioNoExiste();

        MvcResult mvcResult = whenRegistroUsuario("pepe1243",password);

        thenRegistroExitoso(mvcResult,"login");
    }


    @Test
    public void siNoHayNombreDeUsuarioElRegistroFalla() throws Exception {

        MvcResult mvcResult = whenRegistroUsuario("",password);

        thenRegistroFallido(mvcResult,"El usuario no puede estar vacio");
    }


    @Test
    public void siNoHayPasswordElRegistroFalla() throws Exception {

        MvcResult mvcResult= whenRegistroUsuario(usuario,"");

        thenRegistroFallido(mvcResult,"La contrasenia no puede estar vacia");
    }

   @Test
    public void queAlExistirElNombreDeUsuarioIndicadoSeGenereUnError() throws Exception {

        when(registroService.registrar("lucas","password123")).thenThrow(new UsuarioExistenteException());
       //MvcResult mvcResult = whenRegistroUsuario("pepe1235421",password);
       //thenUsuarioExiste(mvcResult, "El usuario ya existe");
    }


    @Test
    public void queAlVerificarLosDatosExitosamenteRedireccioneAVistaLogin() throws Exception {
        MvcResult mvcResult = whenRegistroUsuario("pepe1234",password);
        thenRedireccionaAVistaIndicada(mvcResult,"login");
    }



    private void thenUsuarioExiste(MvcResult mvcResult, String mensaje) {
        ModelAndView mav = mvcResult.getModelAndView();
        assertThat(mav.getModel().get("error").toString(),equalToIgnoringCase(mensaje));
    }

    private ModelAndView whenRedireccionarAVistaLogin() {
        return this.registroController.RedireccionarAVistaLogin();
    }

    private void thenViajaAVistaIndicada(ModelAndView mav, String vista) {
        assertThat(mav.getViewName(),equalToIgnoringCase(vista));
    }


    private void givenUsuarioNoExiste() {
    }

    private void thenRegistroExitoso(MvcResult mvcResult, String vista) {
        ModelAndView mav = mvcResult.getModelAndView();
        assertThat(mav.getViewName(),equalToIgnoringCase(vista));
    }

    private MvcResult whenRegistroUsuario(String nombre, String password) throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/procesarRegistro").
                param("nombre",nombre).
                param("password",password)).andExpect(status().isOk()).andReturn();
        return mvcResult;
    }

    private void thenRegistroFallido(MvcResult mvcResult, String mensajeError) {
        ModelAndView mav = mvcResult.getModelAndView();
        assertThat(mav.getModel().get("error").toString(),equalToIgnoringCase(mensajeError));
    }

    private ModelAndView whenMostrarVistaRegistro() {
        return this.registroController.mostrarRegistro();
    }


    private void thenRedireccionaAVistaIndicada(MvcResult mvcResult, String login) {
        ModelAndView mav = mvcResult.getModelAndView();
        assertThat(mav.getViewName(),equalToIgnoringCase(login));
    }
}
