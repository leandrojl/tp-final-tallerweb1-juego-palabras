package dominio;

import com.tallerwebi.dominio.ServicioPerfil;
import com.tallerwebi.dominio.ServicioPerfilImpl;
import com.tallerwebi.dominio.Usuario;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ServicioPerfilTest {

ServicioPerfil servicioPerfil = new ServicioPerfilImpl();

    @Test
    public void obtenerDatosDePerfil() {
        givenIDUsuario();
        Map<String, Object> usuarioObtenido = whenObtieneDatos();
        thenElUsuarioExiste(usuarioObtenido);
    }


    private void thenElUsuarioExiste(Map<String, Object> usuarioObtenido) {
        assertThat(usuarioObtenido,is(notNullValue()));
    }

    private Map<String, Object> whenObtieneDatos() {
        Map<String, Object> usuarioObtenido = servicioPerfil.obtenerDatosDePerfil();
        return usuarioObtenido;
    }

    private void givenIDUsuario() {
    }


}
