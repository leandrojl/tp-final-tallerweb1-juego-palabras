package com.tallerwebi.presentacion;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.DTO.JugadorPuntajeDto;
import com.tallerwebi.dominio.DTO.RondaDto;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import com.tallerwebi.presentacion.JuegoController;
import org.springframework.web.servlet.ModelAndView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

public class JuegoControllerTest {

    private RondaService rondaServicio;
    private PuntajeService puntajeServicio;
    private PartidaService partidaServicio;
    private UsuarioPartidaService usuarioPartidaService;
    private UsuarioService usuarioService;
    private JuegoController juegoController;

    @BeforeEach
    public void setUp() {
        rondaServicio = mock(RondaService.class);
        puntajeServicio = mock(PuntajeService.class);
        partidaServicio = mock(PartidaService.class);
        usuarioPartidaService = mock(UsuarioPartidaService.class);
        usuarioService = mock(UsuarioService.class);

        juegoController = new JuegoController(rondaServicio, puntajeServicio, partidaServicio, usuarioPartidaService, usuarioService);
    }

    @Test
    public void dadoSesionConUsuarioYPartidaValidaCargaDatosDePartidaExistente() {
        HttpSession session = sesionConUsuarioConPartida(1L, "usuario1", 200L);
        Partida partidaMock = new Partida();
        partidaMock.setId(200L);
        partidaMock.setEstado(Estado.EN_CURSO);
        when(partidaServicio.obtenerPartidaPorId(200L)).thenReturn(partidaMock);

        RondaDto rondaDto = crearRondaDto("palabraExistente", "definicionExistente", 2, "usuario1", 20);
        when(partidaServicio.iniciarNuevaRonda(200L)).thenReturn(rondaDto); // 👈 CAMBIO ACÁ

        ModelAndView mav = mostrarVistaJuego(session);

        verificarVistaJuegoConDatos(mav, "usuario1", 200L, "palabraExistente", "definicionExistente", 2, 20);
    }

    @Test
    public void dadoSesionSinUsuarioRedirigeALogin() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("idUsuario")).thenReturn(null);
        when(session.getAttribute("idUsuario")).thenReturn(null);

        ModelAndView mav = mostrarVistaJuego(session);

        assertEquals("redirect:/login", mav.getViewName());
    }

    @Test
    public void dadoUsuarioYPartidaAbandonaPartidaYLimpiaSesion() {
        Long idUsuario = 1L;
        Long idPartida = 2L;
        HttpSession session = mock(HttpSession.class);

        // Agregá esto si el método usa la sesión para obtener los datos
        when(session.getAttribute("idUsuario")).thenReturn(idUsuario);
        when(session.getAttribute("idPartida")).thenReturn(idPartida);

        UsuarioPartida relacion = new UsuarioPartida();
        when(usuarioPartidaService.obtenerUsuarioEspecificoPorPartida(idUsuario, idPartida)).thenReturn(relacion);

        ResponseEntity<String> response = juegoController.abandonarPartida(idUsuario, idPartida, session);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("OK", response.getBody());
        verify(usuarioPartidaService).marcarComoPerdedor(idUsuario, idPartida);
        verify(session).removeAttribute("idPartida");
    }

    @Test
    public void dadoUsuarioNoRelacionadoConPartidaDevuelveNotFound() {
        Long idUsuario = 1L;
        Long idPartida = 2L;
        HttpSession session = mock(HttpSession.class);
        when(usuarioPartidaService.obtenerUsuarioEspecificoPorPartida(idUsuario, idPartida)).thenReturn(null);

        ResponseEntity<String> response = juegoController.abandonarPartida(idUsuario, idPartida, session);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("No encontrado", response.getBody());
        verify(session, never()).removeAttribute("idPartida");
        verify(usuarioPartidaService, never()).marcarComoPerdedor(anyLong(), anyLong());
    }

    @Test
    public void dadoFalloAlIniciarRondaRedirigeAJuegoYLimpiaSesion() {
        HttpSession session = sesionConUsuarioConPartida(1L, "usuario1", 200L);

        when(partidaServicio.iniciarNuevaRonda(200L)).thenReturn(null);

        ModelAndView mav = juegoController.mostrarVistaJuego(session);

        assertEquals("redirect:/juego", mav.getViewName());
        verify(session).removeAttribute("idPartida");
    }

    // Métodos privados Given

    private HttpSession sesionConUsuarioSinPartida(Long idUsuario, String nombreUsuario) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("idUsuario")).thenReturn(idUsuario);
        when(session.getAttribute("usuario")).thenReturn(nombreUsuario);
        when(session.getAttribute("idPartida")).thenReturn(null);
        return session;
    }

    private HttpSession sesionConUsuarioConPartida(Long idUsuario, String nombreUsuario, Long idPartida) {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("idUsuario")).thenReturn(idUsuario);
        when(session.getAttribute("usuario")).thenReturn(nombreUsuario);
        when(session.getAttribute("idPartida")).thenReturn(idPartida);
        return session;
    }

    private RondaDto crearRondaDto(String palabra, String definicion, int rondaNum, String nombreJugador, int puntaje) {
        RondaDto rondaDto = new RondaDto();
        rondaDto.setPalabra(palabra);
        rondaDto.setDefinicionTexto(definicion);
        rondaDto.setNumeroDeRonda(rondaNum);

        JugadorPuntajeDto jugador = new JugadorPuntajeDto();
        jugador.setNombre(nombreJugador);
        jugador.setPuntaje(puntaje);

        rondaDto.setJugadores(List.of(jugador));
        return rondaDto;
    }

    // Métodos privados When

    private ModelAndView mostrarVistaJuego(HttpSession session) {
        return juegoController.mostrarVistaJuego(session);
    }

    // Métodos privados Then

    private void verificarVistaJuegoConDatos(ModelAndView mav, String usuarioEsperado, Long partidaEsperada,
                                             String palabraEsperada, String definicionEsperada, int rondaEsperada, int puntajeEsperado) {
        assertEquals("juego", mav.getViewName());
        assertNotNull(mav.getModel());
        assertEquals(usuarioEsperado, mav.getModel().get("usuario"));
        assertEquals(partidaEsperada, mav.getModel().get("idPartida"));
        assertEquals(palabraEsperada, mav.getModel().get("palabra"));
        assertEquals(definicionEsperada, mav.getModel().get("definicion"));
        assertEquals(rondaEsperada, mav.getModel().get("rondaActual"));
        assertEquals(puntajeEsperado, mav.getModel().get("puntaje"));
    }

    private void verificarSesionContieneidPartida(HttpSession session, Long idPartidaEsperado) {
        verify(session).setAttribute("idPartida", idPartidaEsperado);
    }

    private void verificarUsuarioAsociadoConPartida(UsuarioPartidaService usuarioPartidaServiceMock) {
        verify(usuarioPartidaServiceMock, times(1)).asociarUsuarioConPartida(any(Usuario.class), any(Partida.class));
    }
}
