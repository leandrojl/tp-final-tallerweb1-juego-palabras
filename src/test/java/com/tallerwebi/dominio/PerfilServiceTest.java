package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

import com.tallerwebi.infraestructura.UsuarioPartidaRepositoryImpl;
import com.tallerwebi.infraestructura.UsuarioRepositoryImpl;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class PerfilServiceTest {
    SessionFactory sessionFactory;
    @InjectMocks
    private PerfilServiceImpl perfilService;
    @Mock
    private UsuarioRepositoryImpl usuarioRepository;
    @Mock
    private UsuarioPartidaRepositoryImpl usuarioPartidaRepository;
    @BeforeEach
    public void setUp() {
    MockitoAnnotations.initMocks(this);
}


    @Test
    public void obtenerUsuarioPorId(){
        Usuario usuario = givenIdUsuario();
        Mockito.when(usuarioRepository.buscarPorId(1)).thenReturn(usuario);
        Usuario usuarioObtenido = whenObtieneDatos(1);
        thenElUsuarioExiste(usuarioObtenido);
    }
    @Test
    public void obtenerWinrate(){
        givenIdUsuario();
        double winrateObtenido = whenObtieneWinrate(1);
        thenComparaWinrate(winrateObtenido, 50.00);
    }
    @Test
    public void dadoUnUsuarioObtenerUnMapaConSusDatos() {
        Usuario usuario = givenIdUsuario();
        Mockito.when(usuarioPartidaRepository.getWinrate(usuario)).thenReturn(50.0);
        Map<String, Object> datos= perfilService.obtenerDatosDePerfil(usuario);
        thenComparaDatos(datos);
    }

    private void thenComparaDatos(Map<String, Object> datos) {
        assertThat(datos.get("nombre"), is("Juan"));
        assertThat(datos.get("winrate"), equalTo(50.0));
    }



    private void thenComparaWinrate(double winrateObtenido, double v) {
    assertThat(winrateObtenido, equalTo(v));
    }

    private double whenObtieneWinrate(int i) {
    return perfilService.obtenerWinrate(i);
    }

    private void thenElUsuarioExiste(Usuario usuarioObtenido) {
        assertThat(usuarioObtenido, notNullValue());
    }

    private Usuario whenObtieneDatos(int id) {

       return perfilService.buscarDatosDeUsuarioPorId(1);
    }

    private Usuario givenIdUsuario() {
    return new Usuario("Juan", "asd@asd", "asdd1234",10);
    }



}
