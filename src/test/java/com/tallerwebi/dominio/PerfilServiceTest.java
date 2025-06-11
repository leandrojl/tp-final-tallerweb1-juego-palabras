package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

import com.tallerwebi.infraestructura.UsuarioRepositoryImpl;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@Rollback
public class PerfilServiceTest {

    SessionFactory sessionFactory;
    private UsuarioRepository usuarioRepository;
    private PerfilService perfilService = new PerfilServiceImpl((UsuarioRepositoryImpl) usuarioRepository);



    @Test
    public void obtenerDatosDePerfil(){
        givenIdUsuario();
        Usuario usuarioObtenido = whenObtieneDatos(1);
        thenElUsuarioExiste(usuarioObtenido);
    }

    @Test
    public void obtenerWinrate(){
        givenIdUsuario();
        double winrateObtenido = whenObtieneWinrate(1);
        thenComparaWinrate(winrateObtenido, 50.00);
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

    private void givenIdUsuario() {

    }



}
