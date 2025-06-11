package dominio;

import com.tallerwebi.dominio.PerfilService;
import com.tallerwebi.dominio.PerfilServiceImpl;
import com.tallerwebi.dominio.UsuarioRepository;
import com.tallerwebi.infraestructura.UsuarioRepositoryImpl;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class PerfilServiceTest {
    private UsuarioRepository usuarioRepository;
    private PerfilService perfilService = new PerfilServiceImpl((UsuarioRepositoryImpl) usuarioRepository);


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
        Map<String, Object> usuarioObtenido = perfilService.obtenerDatosDePerfil();
        return usuarioObtenido;
    }

    private void givenIDUsuario() {
    }


}
