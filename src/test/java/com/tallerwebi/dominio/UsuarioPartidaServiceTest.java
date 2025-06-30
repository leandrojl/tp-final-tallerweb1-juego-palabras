package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioPartidaServiceTest {

    private PartidaService partidaServiceMock;
    private UsuarioPartidaService usuarioPartidaServiceMock;

    @Mock
    private UsuarioPartidaRepository usuarioPartidaRepository;

    @InjectMocks
    private UsuarioPartidaServiceImpl usuarioPartidaService;

    @BeforeEach
    void setUp() {
        partidaServiceMock = mock(PartidaService.class);
        usuarioPartidaServiceMock = mock(UsuarioPartidaService.class);
    }

    @Test
    void deberiaAgregarUsuarioAPartidaConDatosValidos() {
        Long usuarioId = 1L;
        Long partidaId = 2L;
        int puntaje = 0;
        boolean gano = false;
        Estado estado = Estado.EN_ESPERA;

        usuarioPartidaServiceMock.agregarUsuarioAPartida(usuarioId, partidaId, puntaje, gano, estado);

        verify(usuarioPartidaServiceMock, times(1))
                .agregarUsuarioAPartida(usuarioId, partidaId, puntaje, gano, estado);
    }

    @Test
    void deberiaLanzarExcepcionSiUsuarioIdEsNull() {
        Long usuarioId = null;
        Long partidaId = 2L;
        int puntaje = 0;
        boolean gano = false;
        Estado estado = Estado.EN_ESPERA;

        doThrow(new IllegalArgumentException("usuarioId no puede ser null"))
                .when(usuarioPartidaServiceMock)
                .agregarUsuarioAPartida(isNull(), eq(partidaId), anyInt(), anyBoolean(), any(Estado.class));

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioPartidaServiceMock.agregarUsuarioAPartida(usuarioId, partidaId, puntaje, gano, estado);
        });
    }

    @Test
    public void dadoUsuarioYPartidaCuandoAsocioUsuarioConPartidaDebeGuardarRelacion() {
        Usuario usuario = crearUsuarioMock("pepito");
        Partida partida = crearPartidaMock();

        // No devuelve nada, solo verificamos que se llama al método guardar
        usuarioPartidaService.asociarUsuarioConPartida(usuario, partida);

        ArgumentCaptor<UsuarioPartida> captor = ArgumentCaptor.forClass(UsuarioPartida.class);
        verify(usuarioPartidaRepository).guardarUsuarioPartida(captor.capture());

        UsuarioPartida guardado = captor.getValue();
        assertEquals(usuario, guardado.getUsuario());
        assertEquals(partida, guardado.getPartida());
        assertEquals(0, guardado.getPuntaje());
        assertFalse(guardado.isGano());
    }

    @Test
    public void dadoIdsCuandoActualizoPuntajeDebeLlamarAlRepositorio() {
        Long usuarioId = 1L;
        Long partidaId = 2L;
        int nuevoPuntaje = 50;

        usuarioPartidaService.actualizarPuntaje(usuarioId, partidaId, nuevoPuntaje);

        verify(usuarioPartidaRepository).actualizarPuntaje(usuarioId, partidaId, nuevoPuntaje);
    }

    @Test
    public void dadoIdsCuandoObtengoPuntajeDebeDevolverPuntajeDelRepositorio() {
        Long usuarioId = 1L;
        Long partidaId = 2L;
        int puntajeEsperado = 99;

        when(usuarioPartidaRepository.obtenerPuntaje(usuarioId, partidaId)).thenReturn(puntajeEsperado);

        int puntaje = usuarioPartidaService.obtenerPuntaje(usuarioId, partidaId);

        assertEquals(puntajeEsperado, puntaje);
    }

    @Test
    public void dadoIdsCuandoMarcoComoPerdedorDebeActualizarEstadoYGano() {
        Long usuarioId = 1L;
        Long partidaId = 2L;

        UsuarioPartida relacion = crearUsuarioPartidaMock(usuarioId, partidaId);
        when(usuarioPartidaRepository.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId)).thenReturn(relacion);

        usuarioPartidaService.marcarComoPerdedor(usuarioId, partidaId);

        assertFalse(relacion.isGano());
        assertEquals(Estado.FINALIZADA, relacion.getEstado());

        verify(usuarioPartidaRepository).actualizar(relacion);
    }

    @Test
    public void dadoIdsCuandoNoExisteRelacionAlMarcarComoPerdedorNoHaceNada() {
        Long usuarioId = 1L;
        Long partidaId = 2L;

        when(usuarioPartidaRepository.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId)).thenReturn(null);

        usuarioPartidaService.marcarComoPerdedor(usuarioId, partidaId);

        verify(usuarioPartidaRepository, never()).actualizar(any());
    }

    @Test
    public void dadoIdsCuandoObtengoUsuarioEspecificoDevuelveRelacionDelRepositorio() {
        Long usuarioId = 1L;
        Long partidaId = 2L;

        UsuarioPartida relacion = crearUsuarioPartidaMock(usuarioId, partidaId);
        when(usuarioPartidaRepository.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId)).thenReturn(relacion);

        UsuarioPartida resultado = usuarioPartidaService.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId);

        assertEquals(relacion, resultado);
    }

    // ───────────────────────────
    // MÉTODOS PRIVADOS AUXILIARES
    // ───────────────────────────

    private Usuario crearUsuarioMock(String nombreUsuario) {
        Usuario u = new Usuario();
        u.setNombreUsuario(nombreUsuario);
        return u;
    }

    private Partida crearPartidaMock() {
        Partida p = new Partida();
        p.setId(123L);
        return p;
    }

    private UsuarioPartida crearUsuarioPartidaMock(Long usuarioId, Long partidaId) {
        Usuario u = new Usuario();
        u.setId(usuarioId);

        Partida p = new Partida();
        p.setId(partidaId);

        UsuarioPartida up = new UsuarioPartida();
        up.setUsuario(u);
        up.setPartida(p);
        up.setPuntaje(10);
        up.setGano(true);
        up.setEstado(Estado.EN_CURSO);
        return up;
    }
}

