package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.ServicioImplementacion.LobbyServiceImpl;
import com.tallerwebi.dominio.excepcion.PartidaAleatoriaNoDisponibleException;
import com.tallerwebi.dominio.interfaceRepository.LobbyRepository;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.model.Partida;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class LobbyServiceTest {

    private LobbyService lobbyServiceMock;
    private LobbyService lobbyService;
    private LobbyRepository lobbyRepository;
    private PartidaRepository partidaRepo;

    @BeforeEach
    void setUp() {
        lobbyRepository = Mockito.mock(LobbyRepository.class);
        lobbyServiceMock = mock(LobbyService.class);
        partidaRepo = Mockito.mock(PartidaRepository.class);
        lobbyService = new LobbyServiceImpl(lobbyRepository, partidaRepo);
    }

    @Test
    public void testObtenerPartidasEnEsperaConMock() {

        when(lobbyServiceMock.obtenerPartidasEnEspera()).thenReturn(List.of(
                new Partida("Partida en espera 1", "Ingles", true, 5,5, 2, Estado.EN_ESPERA),
                new Partida("Partida en espera 2", "Ingles", false, 3,5, 4, Estado.EN_ESPERA),
                new Partida("Partida en espera 3", "Ingles", true, 7,5, 3, Estado.EN_ESPERA)
        ));

        List<Partida> partidas = lobbyServiceMock.obtenerPartidasEnEspera();

        Assertions.assertEquals(3, partidas.size());

        Assertions.assertEquals(Estado.EN_ESPERA, partidas.get(0).getEstado());
        Assertions.assertEquals(Estado.EN_ESPERA, partidas.get(1).getEstado());
        Assertions.assertEquals(Estado.EN_ESPERA, partidas.get(2).getEstado());

        verify(lobbyServiceMock, times(1)).obtenerPartidasEnEspera();
    }

    @Test
    public void queSePuedanObtenerPartidasDesdeElServicioMock() {

        // Dado que tengo una lista de partidas mockeada
        List<Partida> partidasMock = List.of(new Partida(), new Partida(), new Partida());

        when(lobbyServiceMock.obtenerPartidasEnEspera()).thenReturn(partidasMock);

        assertNotNull(partidasMock);
        assertEquals(3, partidasMock.size());
    }


    //Tests de servicio de eric
    @Test
    public void queSePuedaObtenerUnaPartidaAleatoria(){
        Long idPartidaEsperada = givenExistePartidaAleatoria();

        Long idPartidaObtenida = lobbyService.obtenerUnaPartidaAleatoria();

        thenPartidaAleatoriaObtenidaExitosamente(idPartidaObtenida,idPartidaEsperada);
    }

    @Test
    public void siNoHayPartidaAleatoriaDisponibleQueLanceExcepcion(){
        givenNoExistePartidaAleatoriaDisponible();

        assertThrows(PartidaAleatoriaNoDisponibleException.class,
                () -> lobbyService.obtenerUnaPartidaAleatoria());
    }

    private void givenNoExistePartidaAleatoriaDisponible() {
        when(partidaRepo.obtenerPartidaAleatoria()).thenReturn(null);
    }

    private void thenPartidaAleatoriaObtenidaExitosamente(Long idPartidaObtenida, Long idPartidaEsperada) {
        assertEquals(idPartidaObtenida, idPartidaEsperada);
    }

    private Long givenExistePartidaAleatoria() {
        Partida partida = new Partida("partida1","Español", true, 5, 5, 2, Estado.EN_CURSO);
        when(partidaRepo.obtenerPartidaAleatoria()).thenReturn(partida);
        return partida.getId();
    }

}
