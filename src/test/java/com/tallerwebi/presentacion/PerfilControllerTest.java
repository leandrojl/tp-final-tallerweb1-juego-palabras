package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.interfaceService.PerfilService;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class PerfilControllerTest {

    PerfilService perfilService = mock(PerfilService.class);

    @Test
    public void queDevuelvaLaVistaDePerfilDeUsuario(){
        PerfilController controladorPerfil = new PerfilController(perfilService);
        ModelAndView retorno = controladorPerfil.irAPerfil();
        assertThat(retorno.getViewName(), equalTo("perfil"));
    }
    @Test
    public void queDevuelvaUnModeloDePerfil(){
        PerfilController controladorPerfil = new PerfilController(perfilService);
        ModelAndView retorno = controladorPerfil.irAPerfil();
        assertThat(retorno.getModel(), notNullValue());
    }
    @Test
    public void queDevuelvaUnModeloConDatosDePerfil(){
        PerfilController controladorPerfil = new PerfilController(perfilService);
        ModelAndView retorno = controladorPerfil.irAPerfil();
        Map<String, Object> modelo=retorno.getModel();

    }

}
