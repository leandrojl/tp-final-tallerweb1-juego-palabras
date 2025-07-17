package com.tallerwebi.dominio;

import com.tallerwebi.dominio.DTO.DtoIntento;
import com.tallerwebi.dominio.DTO.JugadorPuntajeDto;
import com.tallerwebi.dominio.DTO.ResultadoIntentoDto;
import com.tallerwebi.dominio.DTO.RondaDto;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.ServicioImplementacion.PartidaServiceImpl;
import com.tallerwebi.dominio.excepcion.PartidaInexistenteException;
import com.tallerwebi.dominio.excepcion.RondaFinalizadaException;
import com.tallerwebi.dominio.excepcion.RondaInexistenteException;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.RondaRepository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PartidaServiceTest {
    private SimpMessagingTemplate simpMessagingTemplate;
    private PartidaRepository partidaRepository;
    private RondaService rondaService;
    private RondaRepository rondaRepository;
    private ScheduledExecutorService timerRonda;
    private PartidaServiceImpl service;
    private PartidaServiceImpl partidaService;
    private UsuarioPartidaRepository usuarioPartidaRepository;
    private AciertoService aciertoService;
    private UsuarioPartidaService usuarioPartidaService;
    private RondaTimerManager rondaTimerManager;
    private GeminiBotService botService;
    private UsuarioService usuarioService;


    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        // Mocks
        usuarioService = mock(UsuarioService.class);
        simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        partidaRepository = mock(PartidaRepository.class);
        rondaService = mock(RondaService.class);
        rondaRepository = mock(RondaRepository.class);
        usuarioPartidaRepository = mock(UsuarioPartidaRepository.class);
        aciertoService = mock(AciertoService.class);
        usuarioPartidaService = mock(UsuarioPartidaService.class);
        partidaService = mock(PartidaServiceImpl.class);

        timerRonda = mock(ScheduledExecutorService.class);
        rondaTimerManager = mock(RondaTimerManager.class);
        this.botService = mock(GeminiBotService.class);
        // Service as spy to verify finalizeRonda
        service = spy(new PartidaServiceImpl(
                simpMessagingTemplate,
                partidaRepository,
                rondaService,
                rondaRepository,
                usuarioPartidaRepository,
                aciertoService,
                usuarioPartidaService,
                rondaTimerManager,
                botService,
                usuarioService
        ));

        partidaService = new PartidaServiceImpl(
                simpMessagingTemplate,
                partidaRepository,
                rondaService,
                rondaRepository,
                usuarioPartidaRepository,
                aciertoService,
                usuarioPartidaService,
                rondaTimerManager,
                botService,
                usuarioService
        );


        Field usuarioPartidaRepoField = PartidaServiceImpl.class.getDeclaredField("usuarioPartidaRepository");
        usuarioPartidaRepoField.setAccessible(true);
        usuarioPartidaRepoField.set(partidaService, usuarioPartidaRepository);

    }

    @Test
    void queLaRondaGenereUnDtoConDatos() {
        Ronda ronda = whenTengoUnaPartidaRondaYPalabra(1L);
        //ejecucion
        RondaDto dto = service.iniciarNuevaRonda(1L);
        //comprobacion
        assertEquals("gato", dto.getPalabra());
        assertEquals("Animal doméstico", dto.getDefinicionTexto());
        assertEquals(1, dto.getNumeroDeRonda());
    }

    @Test
    void queSeEjecuteFinalizarRondaSiPasaElTiempoEstipulado() {
        //Preparacion
        Long partidaId = 1L;
        Ronda ronda = whenTengoUnaPartidaRondaYPalabra(partidaId);


        ScheduledFuture<?> futureMock = mock(ScheduledFuture.class);
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        doReturn(futureMock)
                .when(timerRonda)
                .schedule(captor.capture(), anyLong(), any(TimeUnit.class));

        //Ejecucion
        service.iniciarNuevaRonda(partidaId);

        //Verificación de que se agendo la tarea, y se ejecuto.
        verify(timerRonda).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
        assertSame(futureMock, service.getFinalizarRondaPorTimer());
        captor.getValue().run();
        verify(service).finalizarRonda(ronda);
    }

    private Ronda whenTengoUnaPartidaRondaYPalabra(Long partidaId) {
        Partida partida = new Partida();
        partida.setIdioma("es");
        Palabra palabra = new Palabra();
        palabra.setDescripcion("gato");
        palabra.setDefiniciones(new ArrayList<>(Set.of(new Definicion("Animal doméstico"))));
        Ronda ronda = new Ronda();
        ronda.setNumeroDeRonda(1);
        ronda.setPalabra(palabra);
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaService.crearRonda(partidaId, "es")).thenReturn(ronda);
        return ronda;
    }


    @Test
    public void dadoPartidaValidaCuandoSeIniciaNuevaRondaDevuelveRondaDtoConDatosCorrectos() throws Exception {
        Long partidaId = 1L;

        Partida partidaMock = crearPartidaMock("Castellano");
        Palabra palabra = crearPalabraConDefinicion("Casa", "Lugar donde vive la gente");
        Ronda ronda = crearRonda(palabra, 1);
        UsuarioPartida usuarioPartida = crearUsuarioPartida("july3p", 20);

        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partidaMock);
        when(rondaService.crearRonda(partidaId, "Castellano")).thenReturn(ronda);
        when(usuarioPartidaRepository.obtenerUsuarioPartidaPorPartida(partidaId)).thenReturn(List.of(usuarioPartida));

        // Inyección por reflexión
        Field usuarioPartidaRepoField = PartidaServiceImpl.class.getDeclaredField("usuarioPartidaRepository");
        usuarioPartidaRepoField.setAccessible(true);
        usuarioPartidaRepoField.set(partidaService, usuarioPartidaRepository);

        RondaDto resultado = partidaService.iniciarNuevaRonda(partidaId);

        assertNotNull(resultado);
        assertEquals("Casa", resultado.getPalabra());
        assertEquals("Lugar donde vive la gente", resultado.getDefinicionTexto());
        assertEquals(1, resultado.getNumeroDeRonda());

        List<JugadorPuntajeDto> jugadores = resultado.getJugadores();
        assertEquals(1, jugadores.size());

        JugadorPuntajeDto jugador = jugadores.get(0);
        assertEquals("july3p", jugador.getNombre());
        assertEquals(20, jugador.getPuntaje());

        // Capturamos las llamadas a convertAndSend
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(simpMessagingTemplate, times(2)).convertAndSend(eq("/topic/juego/" + partidaId), captor.capture());

        List<Object> enviados = captor.getAllValues();

        // Verificamos que uno de los mensajes es el RondaDto esperado
        boolean encontroRondaDto = enviados.stream().anyMatch(o -> o instanceof RondaDto);

        assertTrue(encontroRondaDto, "No se envió RondaDto por simpMessagingTemplate");

        // Opcional: Podés verificar también el otro tipo de mensaje si querés
    }


    @Test
    public void dadoPartidaInexistenteCuandoSeIniciaNuevaRondaLanzaExcepcion() {
        Long partidaId = 999L;
        when(partidaRepository.buscarPorId(partidaId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> partidaService.iniciarNuevaRonda(partidaId));

        assertEquals("No existe la partida con ID: " + partidaId, ex.getMessage());
    }

//    @Test
//    public void dadoPartidaIdCuandoSeObtieneRondaActualDevuelveLaDefinicionCorrecta() {
//        Long partidaId = 77L;
//
//        RondaDto dto = new RondaDto();
//        dto.setPalabra("Sol");
//        dto.setDefinicionTexto("Estrella del sistema solar");
//        dto.setNumeroDeRonda(2);
//
//        // Inyectamos el DTO en el mapa privado vía método de test
//        partidaService.obtenerMapaDefinicionesParaTest().put(partidaId, dto);
//
//        RondaDto resultado = partidaService.obtenerPalabraYDefinicionDeRondaActual(partidaId);
//
//        assertNotNull(resultado);
//        assertEquals("Sol", resultado.getPalabra());
//        assertEquals("Estrella del sistema solar", resultado.getDefinicionTexto());
//        assertEquals(2, resultado.getNumeroDeRonda());
//    }


    // ──────────────────────
    // MÉTODOS PRIVADOS AUXILIARES
    // ──────────────────────

    private Partida crearPartidaMock(String idioma) {
        Partida partida = new Partida();
        partida.setIdioma(idioma);
        return partida;
    }

    private Palabra crearPalabraConDefinicion(String descripcion, String definicionTexto) {
        Palabra palabra = new Palabra();
        palabra.setDescripcion(descripcion);

        Definicion definicion = new Definicion();
        definicion.setDefinicion(definicionTexto);

        palabra.setDefiniciones(List.of(definicion));
        return palabra;
    }

    private Ronda crearRonda(Palabra palabra, int numero) {
        Ronda ronda = new Ronda();
        ronda.setPalabra(palabra);
        ronda.setNumeroDeRonda(numero);
        return ronda;
    }

    private UsuarioPartida crearUsuarioPartida(String nombreUsuario, int puntaje) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario);

        UsuarioPartida up = new UsuarioPartida();
        up.setUsuario(usuario);
        up.setPuntaje(puntaje);
        return up;
    }

    // === TESTS DE PROCESAR INTENTO === //


    @Test
    public void procesarIntento_LanzaException_SiPartidaNoExiste() {
        Long idPartida = 999L;
        DtoIntento dto = new DtoIntento("hola", 3L, idPartida, 3);
        when(partidaRepository.buscarPorId(idPartida)).thenReturn(null);

        PartidaInexistenteException ex = assertThrows(PartidaInexistenteException.class,
                () -> partidaService.procesarIntento(dto, "pepito"));

        assertEquals("Partida no encontrada con id: " + idPartida, ex.getMessage());
    }

    @Test
    public void procesarIntento_LanzaException_SiRondaNoExiste() {
        Long idPartida = 999L;
        DtoIntento dto = new DtoIntento("hola", 3L, idPartida, 3);
        Partida partida = crearPartidaMock("ingles");
        when(partidaRepository.buscarPorId(idPartida)).thenReturn(partida);
        when(rondaRepository.obtenerUltimaRondaDePartida(idPartida)).thenReturn(null);

        RondaInexistenteException ex = assertThrows(RondaInexistenteException.class,
                () -> partidaService.procesarIntento(dto, "pepito"));

        assertEquals("Ronda no encontrada", ex.getMessage());
    }

    @Test
    public void procesarIntento_LanzaException_SiRondaEstaFinalizada() {
        Long idPartida = 999L;
        DtoIntento dto = new DtoIntento("hola", 3L, idPartida, 3);
        Partida partida = crearPartidaMock("ingles");
        Palabra palabra = crearPalabraConDefinicion("hola", "holas");
        Ronda ronda = crearRonda(palabra, 1);
        ronda.setEstado(Estado.FINALIZADA);
        when(partidaRepository.buscarPorId(idPartida)).thenReturn(partida);
        when(rondaService.obtenerUltimaRondaDePartida(idPartida)).thenReturn(ronda);

        RondaFinalizadaException ex = assertThrows(RondaFinalizadaException.class,
                () -> partidaService.procesarIntento(dto, "pepito"));

        assertEquals("Ronda finalizada", ex.getMessage());
    }

    @Test
    void procesarIntento_DeberiaRegistrarAciertoYSumarPuntos_SiEsCorrectoYNoAcertoAntes() {
        Long partidaId = 1L;
        Long usuarioId = 2L;
        Long rondaId = 10L;

        DtoIntento intento = new DtoIntento("gato", usuarioId, partidaId, 1);
        Palabra palabra = crearPalabraConDefinicion("gato", "animal");
        Ronda ronda = crearRonda(palabra, 1);
        ronda.setEstado(Estado.EN_CURSO);
        ronda.setId(rondaId);
        Partida partida = crearPartidaMock("es");

        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaService.obtenerUltimaRondaDePartida(partidaId)).thenReturn(ronda);
        when(aciertoService.jugadorYaAcerto(usuarioId, rondaId)).thenReturn(false);
        when(aciertoService.registrarAcierto(usuarioId, rondaId)).thenReturn(100);

        doNothing().when(service).finalizarRondaEnCasoDeQueTodosAcertaron(partidaId, rondaId);

        service.procesarIntento(intento, "elias");

        verify(aciertoService).registrarAcierto(usuarioId, rondaId);
        verify(usuarioPartidaService).sumarPuntos(usuarioId, partidaId, 100);
    }

    @Test
    void procesarIntento_NoDeberiaRegistrarAcierto_SiJugadorYaAcertoAntes() {
        Long partidaId = 1L;
        Long usuarioId = 2L;
        DtoIntento dto = new DtoIntento("gato", usuarioId, partidaId, 1);
        Partida partida = crearPartidaMock("es");
        Palabra palabra = crearPalabraConDefinicion("gato", "animal");
        Ronda ronda = crearRonda(palabra, 1);
        ronda.setEstado(Estado.EN_CURSO);
        ronda.setId(10L);

        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaService.obtenerUltimaRondaDePartida(partidaId)).thenReturn(ronda);
        when(aciertoService.jugadorYaAcerto(usuarioId, 10L)).thenReturn(true);

        service.procesarIntento(dto, "elias");

        verify(aciertoService, never()).registrarAcierto(any(), any());
        verify(usuarioPartidaService, never()).sumarPuntos(any(), any(), anyInt());
    }

    @Test
    void procesarIntento_DeberiaFinalizarRonda_SiTodosAcertaron() {
        Long partidaId = 1L;
        Long usuarioId = 2L;
        DtoIntento dto = new DtoIntento("gato", usuarioId, partidaId, 1);
        Partida partida = crearPartidaMock("es");
        Palabra palabra = crearPalabraConDefinicion("gato", "animal");
        Ronda ronda = crearRonda(palabra, 1);
        ronda.setEstado(Estado.EN_CURSO);
        ronda.setId(10L);

        when(partidaRepository.buscarPorId(partidaId)).thenReturn(partida);
        when(rondaService.obtenerUltimaRondaDePartida(partidaId)).thenReturn(ronda);
        when(aciertoService.jugadorYaAcerto(usuarioId, 10L)).thenReturn(false);
        when(aciertoService.registrarAcierto(usuarioId, 10L)).thenReturn(10);
        when(usuarioPartidaRepository.buscarPorPartida(partidaId)).thenReturn(List.of());


        doNothing().when(service).finalizarRondaEnCasoDeQueTodosAcertaron(partidaId, 10L);

        service.procesarIntento(dto, "pepito");

        verify(service).finalizarRondaEnCasoDeQueTodosAcertaron(partidaId, 10L);
    }




}




