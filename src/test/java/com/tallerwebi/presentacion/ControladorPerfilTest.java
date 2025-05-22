package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.ServicioPerfilImpl;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;

public class ControladorPerfilTest {

    ServicioPerfil servicioPerfil = new ServicioPerfilImpl();

    @Test
    public void queDevuelvaLaVistaDePerfilDeUsuario(){
        ControladorPerfil controladorPerfil = new ControladorPerfil(servicioPerfil);
        ModelAndView retorno = controladorPerfil.irAPerfil();
        assertThat(retorno.getViewName(), equalTo("perfil"));
    }
    @Test
    public void queDevuelvaUnModeloDePerfil(){
        ControladorPerfil controladorPerfil = new ControladorPerfil(servicioPerfil);
        ModelAndView retorno = controladorPerfil.irAPerfil();
        assertThat(retorno.getModel(), notNullValue());
    }
    @Test
    public void queDevuelvaUnModeloConDatosDePerfil(){
        ControladorPerfil controladorPerfil = new ControladorPerfil(servicioPerfil);
        ModelAndView retorno = controladorPerfil.irAPerfil();
        Map<String, Object> modelo=retorno.getModel();
        assertThat(modelo.get("nombre"), notNullValue());
        assertThat(modelo.get("usuario"), notNullValue());
        assertThat(modelo.get("edad"), notNullValue());
        assertThat(modelo.get("winrate"), notNullValue());
        assertThat(modelo.get("fotoPerfil"), notNullValue());
    }

}
