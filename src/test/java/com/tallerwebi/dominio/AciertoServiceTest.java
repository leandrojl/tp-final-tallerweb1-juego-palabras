package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceRepository.AciertoRepository;
import com.tallerwebi.dominio.interfaceRepository.RondaRepository;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.model.Acierto;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.dominio.model.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AciertoServiceTest {

    @Mock
    private AciertoRepository aciertoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RondaRepository rondaRepository;

    @Mock
    private UsuarioPartidaRepository usuarioPartidaRepository;

    @InjectMocks
    private AciertoServiceImpl aciertoService;


    @Test
    void queJugadorYaAcertoDevuelvaTrueSiExiste() {
        when(aciertoRepository.jugadorYaAcerto(1L, 2L)).thenReturn(true);

        assertTrue(aciertoService.jugadorYaAcerto(1L, 2L));
    }

    @Test
    void queRegistreAciertoYDevuelvaPuntajeCorrecto() {
        Long idUsuario = 1L;
        Long idRonda = 2L;

        Usuario usuario = new Usuario();
        Ronda ronda = new Ronda();

        when(aciertoRepository.jugadorYaAcerto(idUsuario, idRonda)).thenReturn(false);
        when(usuarioRepository.buscarPorId(idUsuario)).thenReturn(usuario);
        when(rondaRepository.buscarPorId(idRonda)).thenReturn(ronda);
        when(aciertoRepository.contarAciertosPorRondaId(idRonda)).thenReturn(0); // primer acierto

        int puntos = aciertoService.registrarAcierto(idUsuario, idRonda);

        assertEquals(10, puntos); // primer acierto => 10 pts
        verify(aciertoRepository).registrarAcierto(any(Acierto.class));
    }

    @Test
    void queLanceExcepcionSiUsuarioNoExiste() {
        when(aciertoRepository.jugadorYaAcerto(1L, 2L)).thenReturn(false);
        when(usuarioRepository.buscarPorId(1L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> aciertoService.registrarAcierto(1L, 2L));

        assertTrue(exception.getMessage().contains("Usuario no encontrado"));
    }

    @Test
    void queLanceExcepcionSiRondaNoExiste() {
        Usuario usuario = new Usuario();

        when(aciertoRepository.jugadorYaAcerto(1L, 2L)).thenReturn(false);
        when(usuarioRepository.buscarPorId(1L)).thenReturn(usuario);
        when(rondaRepository.buscarPorId(2L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> aciertoService.registrarAcierto(1L, 2L));

        assertTrue(exception.getMessage().contains("Ronda no encontrada"));
    }


    @Test
    void queTodosAcertaronCuandoLaCantidadCoincide() {
        Long idPartida = 1L;
        Long idRonda = 2L;

        when(usuarioPartidaRepository.contarUsuariosEnPartida(idPartida)).thenReturn((int) 3);
        when(aciertoRepository.contarUsuariosQueAcertaron(idRonda)).thenReturn(3L);

        boolean resultado = aciertoService.todosAcertaron(idPartida, idRonda);

        assertTrue(resultado);
    }

}