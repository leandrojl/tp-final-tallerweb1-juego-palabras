//package com.tallerwebi.presentacion;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.tallerwebi.dominio.Enum.Estado;
//import com.tallerwebi.dominio.DTO.JugadorPuntajeDto;
//import com.tallerwebi.dominio.DTO.RondaDto;
//import com.tallerwebi.dominio.interfaceService.*;
//import com.tallerwebi.dominio.model.Partida;
//import com.tallerwebi.dominio.model.Partida2;
//import com.tallerwebi.dominio.model.Usuario;
//import com.tallerwebi.dominio.model.UsuarioPartida;
//import com.tallerwebi.presentacion.JuegoController;
//import org.springframework.web.servlet.ModelAndView;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import javax.servlet.http.HttpSession;
//import java.io.Serializable;
//import java.util.List;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.HttpStatus;
//
//public class JuegoControllerTest {
//
//    private RondaService rondaServicio;
//    private PuntajeService puntajeServicio;
//    private PartidaService partidaServicio;
//    private UsuarioPartidaService usuarioPartidaService;
//    private UsuarioService usuarioService;
//    private JuegoController juegoController;
//
//    @BeforeEach
//    public void setUp() {
//        rondaServicio = mock(RondaService.class);
//        puntajeServicio = mock(PuntajeService.class);
//        partidaServicio = mock(PartidaService.class);
//        usuarioPartidaService = mock(UsuarioPartidaService.class);
//        usuarioService = mock(UsuarioService.class);
//
//        juegoController = new JuegoController(rondaServicio, puntajeServicio, partidaServicio, usuarioPartidaService, usuarioService);
//    }
//
//    @Test
//    public void dadoSesionConUsuarioYPartidaNulaCreaPartidaNuevaYAsociaUsuario() {
//        HttpSession session = sesionConUsuarioSinPartida(1L, "usuario1");
//        Partida2 partidaGenerada = generarPartidaDummy("usuario1");
//        when(partidaServicio.crearPartida(any())).thenReturn(100L);
//        when(usuarioService.obtenerUsuarioPorId(1L)).thenReturn(new Usuario());
//        when(partidaServicio.iniciarNuevaRonda(100L)).thenReturn(crearRondaDto("palabraTest", "definicionTest", 1, "usuario1", 10));
//
//        ModelAndView mav = mostrarVistaJuego(session);
//
//        verificarVistaJuegoConDatos(mav, "usuario1", 100L, "palabraTest", "definicionTest", 1, 10);
//        verificarSesionContienePartidaId(session, 100L);
//        verificarUsuarioAsociadoConPartida(usuarioPartidaService);
//    }
//
//    @Test
//    public void dadoSesionConUsuarioYPartidaValidaCargaDatosDePartidaExistente() {
//        HttpSession session = sesionConUsuarioConPartida(1L, "usuario1", 200L);
//        Partida partidaMock = new Partida();
//        partidaMock.setId(200L);
//        partidaMock.setEstado(Estado.EN_CURSO);
//        when(partidaServicio.obtenerPartidaPorId(200L)).thenReturn(partidaMock);
//
//        RondaDto rondaDto = crearRondaDto("palabraExistente", "definicionExistente", 2, "usuario1", 20);
//        when(partidaServicio.obtenerPalabraYDefinicionDeRondaActual(200L)).thenReturn(rondaDto);
//
//        ModelAndView mav = mostrarVistaJuego(session);
//
//        verificarVistaJuegoConDatos(mav, "usuario1", 200L, "palabraExistente", "definicionExistente", 2, 20);
//    }
//
//    @Test
//    public void dadoSesionSinUsuarioRedirigeALogin() {
//        HttpSession session = mock(HttpSession.class);
//        when(session.getAttribute("usuarioID")).thenReturn(null);
//        when(session.getAttribute("usuario")).thenReturn(null);
//
//        ModelAndView mav = mostrarVistaJuego(session);
//
//        assertEquals("redirect:/login", mav.getViewName());
//    }
//
//    @Test
//    public void dadoUsuarioYPartidaAbandonaPartidaYLimpiaSesion() {
//        Long idUsuario = 1L;
//        Long partidaId = 2L;
//        HttpSession session = mock(HttpSession.class);
//        UsuarioPartida relacion = new UsuarioPartida();
//
//        when(usuarioPartidaService.obtenerUsuarioEspecificoPorPartida(idUsuario, partidaId)).thenReturn(relacion);
//
//        ResponseEntity<String> response = juegoController.abandonarPartida(idUsuario, partidaId, session);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("OK", response.getBody());
//        verify(usuarioPartidaService).marcarComoPerdedor(idUsuario, partidaId);
//        verify(session).removeAttribute("partidaID");
//    }
//
//    @Test
//    public void dadoUsuarioNoRelacionadoConPartidaDevuelveNotFound() {
//        Long idUsuario = 1L;
//        Long partidaId = 2L;
//        HttpSession session = mock(HttpSession.class);
//        when(usuarioPartidaService.obtenerUsuarioEspecificoPorPartida(idUsuario, partidaId)).thenReturn(null);
//
//        ResponseEntity<String> response = juegoController.abandonarPartida(idUsuario, partidaId, session);
//
//        assertEquals(404, response.getStatusCodeValue());
//        assertEquals("No encontrado", response.getBody());
//        verify(session, never()).removeAttribute("partidaID");
//        verify(usuarioPartidaService, never()).marcarComoPerdedor(anyLong(), anyLong());
//    }
//
//    // Métodos privados Given
//
//    private HttpSession sesionConUsuarioSinPartida(Long idUsuario, String nombreUsuario) {
//        HttpSession session = mock(HttpSession.class);
//        when(session.getAttribute("usuarioID")).thenReturn(idUsuario);
//        when(session.getAttribute("usuario")).thenReturn(nombreUsuario);
//        when(session.getAttribute("partidaID")).thenReturn(null);
//        return session;
//    }
//
//    private HttpSession sesionConUsuarioConPartida(Long idUsuario, String nombreUsuario, Long partidaId) {
//        HttpSession session = mock(HttpSession.class);
//        when(session.getAttribute("usuarioID")).thenReturn(idUsuario);
//        when(session.getAttribute("usuario")).thenReturn(nombreUsuario);
//        when(session.getAttribute("partidaID")).thenReturn(partidaId);
//        return session;
//    }
//
//    private Partida2 generarPartidaDummy(String nombreUsuario) {
//        Partida2 partida = new Partida2();
//        partida.setNombre("Partida de " + nombreUsuario);
//        partida.setIdioma("Castellano");
//        partida.setPermiteComodin(false);
//        partida.setRondasTotales(5);
//        partida.setMaximoJugadores(1);
//        partida.setMinimoJugadores(1);
//        partida.setEstado(Estado.EN_CURSO);
//        return partida;
//    }
//
//    private RondaDto crearRondaDto(String palabra, String definicion, int rondaNum, String nombreJugador, int puntaje) {
//        RondaDto rondaDto = new RondaDto();
//        rondaDto.setPalabra(palabra);
//        rondaDto.setDefinicionTexto(definicion);
//        rondaDto.setNumeroDeRonda(rondaNum);
//
//        JugadorPuntajeDto jugador = new JugadorPuntajeDto();
//        jugador.setNombre(nombreJugador);
//        jugador.setPuntaje(puntaje);
//
//        rondaDto.setJugadores(List.of(jugador));
//        return rondaDto;
//    }
//
//    // Métodos privados When
//
//    private ModelAndView mostrarVistaJuego(HttpSession session) {
//        return juegoController.mostrarVistaJuego(session);
//    }
//
//    // Métodos privados Then
//
//    private void verificarVistaJuegoConDatos(ModelAndView mav, String usuarioEsperado, Long partidaEsperada,
//                                             String palabraEsperada, String definicionEsperada, int rondaEsperada, int puntajeEsperado) {
//        assertEquals("juego", mav.getViewName());
//        assertNotNull(mav.getModel());
//        assertEquals(usuarioEsperado, mav.getModel().get("usuario"));
//        assertEquals(partidaEsperada, mav.getModel().get("partidaId"));
//        assertEquals(palabraEsperada, mav.getModel().get("palabra"));
//        assertEquals(definicionEsperada, mav.getModel().get("definicion"));
//        assertEquals(rondaEsperada, mav.getModel().get("rondaActual"));
//        assertEquals(puntajeEsperado, mav.getModel().get("puntaje"));
//    }
//
//    private void verificarSesionContienePartidaId(HttpSession session, Long partidaIdEsperado) {
//        verify(session).setAttribute("partidaID", partidaIdEsperado);
//    }
//
//    private void verificarUsuarioAsociadoConPartida(UsuarioPartidaService usuarioPartidaServiceMock) {
//        verify(usuarioPartidaServiceMock, times(1)).asociarUsuarioConPartida(any(Usuario.class), any(Partida.class));
//    }
//}
