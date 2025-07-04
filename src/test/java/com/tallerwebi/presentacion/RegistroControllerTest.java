package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.PasswordMenorAOchoCaracteresException;
import com.tallerwebi.dominio.interfaceService.RegistroService;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteException;
import com.tallerwebi.dominio.model.Usuario;
import org.dom4j.rule.Mode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@Transactional
@Rollback
public class RegistroControllerTest {

    private RegistroController registroController;
    private ModelAndView mav;
    private String nombreUsuario = "pepe123";
    private String password = "abcdefgh123546";
    private MockMvc mockMvc;
    private RegistroService registroService;

    @BeforeEach
    public void init(){
        this.registroService = Mockito.mock(RegistroService.class);
        this.registroController = new RegistroController(registroService);
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
    public void queSePuedaRegistrarUnUsuario() {
        Usuario usuario = givenUsuarioExiste();
        ModelAndView mav = registroController.registrar(usuario);
        thenRegistroExitoso(mav,"login");
    }


    @Test
    public void siNoHayNombreDeUsuarioElRegistroFalla() throws Exception {
        Usuario usuario = new Usuario("",password);
        ModelAndView mav = registroController.registrar(usuario);

        thenRegistroFallido(mav,"El usuario no puede estar vacio");
    }


    @Test
    public void siNoHayPasswordElRegistroFalla() throws Exception {
        Usuario usuario = new Usuario(nombreUsuario,"");
        ModelAndView mav = registroController.registrar(usuario);

        thenRegistroFallido(mav,"La contrasenia no puede estar vacia");
    }

   @Test
    public void queAlExistirElNombreDeUsuarioIndicadoSeMuestreMensajeDeError(){
       Usuario usuario = new Usuario(nombreUsuario,password);
        when(registroService.registrar(nombreUsuario,password)).thenThrow(UsuarioExistenteException.class);
        ModelAndView mav = registroController.registrar(usuario);

        assertThat(mav.getModel().get("error").toString(),equalToIgnoringCase("El usuario ya existe"));
        assertThat(mav.getViewName(),equalToIgnoringCase("registro"));
    }

    @Test
    public void siLaContraseñaNoCumpleConLosRequisitosDeLongitudQueSeMuestreMensajeDeError(){
        Usuario usuario = new Usuario(nombreUsuario,"abdsc");
        when(registroService.registrar(nombreUsuario,"abdsc")).thenThrow(PasswordMenorAOchoCaracteresException.class);
        ModelAndView mav = registroController.registrar(usuario);

        assertThat(mav.getModel().get("error").toString(),equalToIgnoringCase("La contraseña debe contener al menos ocho caracteres"));
        assertThat(mav.getViewName(),equalToIgnoringCase("registro"));
    }

    private ModelAndView whenRedireccionarAVistaLogin() {
        return this.registroController.RedireccionarAVistaLogin();
    }

    private void thenViajaAVistaIndicada(ModelAndView mav, String vista) {
        assertThat(mav.getViewName(),equalToIgnoringCase(vista));
    }


    private Usuario givenUsuarioExiste() {
        Usuario usuario = new Usuario(nombreUsuario,password);
        when(registroService.registrar(nombreUsuario,password)).thenReturn(usuario);
        return usuario;
    }

    private void thenRegistroExitoso(ModelAndView mav, String vista) {
        assertThat(mav.getViewName(),equalToIgnoringCase(vista));
    }
    private void thenRegistroFallido(ModelAndView mav, String mensajeError) {
        assertThat(mav.getModel().get("error").toString(),equalToIgnoringCase(mensajeError));
    }

    private ModelAndView whenMostrarVistaRegistro() {
        return this.registroController.mostrarRegistro();
    }
}
