package com.tallerwebi.dominio;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.infraestructura.RondaRepository;
import com.tallerwebi.dominio.interfaceService.RondaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

 public class PartidaServiceTest {
    private SimpMessagingTemplate simpMessagingTemplate;
    private PartidaRepository partidaRepository;
    private RondaService rondaService;
    private RondaRepository rondaRepository;
    private ScheduledExecutorService timerRonda;
    private PartidaServiceImpl service;
    private UsuarioPartidaRepository usuarioPartidaRepository;

    @BeforeEach
    void setUp() {
        // Mocks
        simpMessagingTemplate = mock(SimpMessagingTemplate.class);
        partidaRepository = mock(PartidaRepository.class);
        rondaService = mock(RondaService.class);
        rondaRepository = mock(RondaRepository.class);
        usuarioPartidaRepository = mock(UsuarioPartidaRepository.class);
        timerRonda = mock(ScheduledExecutorService.class);

        // Service as spy to verify finalizeRonda
        service = spy(new PartidaServiceImpl(
                simpMessagingTemplate,
                partidaRepository,
                rondaService,
                rondaRepository,
                usuarioPartidaRepository
        ));

    }
@Test
void queLaRondaGenereUnDtoConDatos(){
   Ronda ronda =whenTengoUnaPartidaRondaYPalabra(1L);
   //ejecucion
   DefinicionDto dto = service.iniciarNuevaRonda(1L);
   //comprobacion
    assertEquals("gato", dto.getPalabra());
    assertEquals("Animal doméstico", dto.getDefinicionTexto());
    assertEquals(1, dto.getNumeroDeRonda());
}
    @Test
    void queSeEjecuteFinalizarRondaSiPasaElTiempoEstipulado() {
        //Preparacion
        Long partidaId = 1L;
        Ronda ronda= whenTengoUnaPartidaRondaYPalabra(partidaId);


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
 }
