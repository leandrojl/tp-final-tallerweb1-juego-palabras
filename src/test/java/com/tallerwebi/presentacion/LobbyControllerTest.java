package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LobbyControllerTest {

    PartidaService partidaServiceMock;
    LobbyService lobbyService;
    LobbyController controladorLobby;

    @BeforeEach
    public void setUp() {
        partidaServiceMock = mock(PartidaService.class);
        lobbyService = mock(LobbyService.class);
        controladorLobby = new LobbyController(partidaServiceMock, lobbyService);
    }

    @Test
    public void queSePuedanVerPartidasEnElLobbyMock() {

        Model model = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);
        List<Partida> partidasMock = List.of(new Partida(), new Partida(), new Partida());

        when(session.getAttribute("usuario")).thenReturn(new Usuario("july3p"));
        model.addAttribute("partidas", partidasMock);
        ModelAndView mav = controladorLobby.Lobby(session, model);

        assertEquals("lobby", mav.getViewName());
        assertTrue(model.containsAttribute("partidas"));

    }

    @Test
    public void queSeMuestreMensajeSiNoHayPartidasEnCurso() {
        Model model = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);


        when(session.getAttribute("jugador")).thenReturn(null);


        when(lobbyService.obtenerPartidasEnEspera()).thenReturn(List.of());

        ModelAndView mav = controladorLobby.Lobby(session, model);


        assertEquals("lobby", mav.getViewName());


        assertTrue(model.containsAttribute("mensaje"));
        String mensaje = (String) model.getAttribute("mensaje");
        assertNotNull(mensaje);
        assertEquals("No hay partidas disponibles en curso.", mensaje);
    }

    @Test
    public void queSoloSeMuestrenPartidasEnEsperaEnElLobby() {
        Model model = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);

        when(session.getAttribute("jugador")).thenReturn(null);

        List<Partida2> partidasMock = List.of(
                new Partida2("Partida 1", "Español", true, 4, 5, 2, Estado.EN_ESPERA),
                new Partida2("Partida 2", "Inglés", false, 3, 5, 3, Estado.EN_ESPERA),
                new Partida2("Partida 3", "Francés", true, 2, 5, 1, Estado.EN_ESPERA)
        );
        when(lobbyService.obtenerPartidasEnEspera()).thenReturn(partidasMock);

        ModelAndView mav = controladorLobby.Lobby(session, model);

        assertEquals("lobby", mav.getViewName());
        assertTrue(model.containsAttribute("partidas"));

        List<Partida2> partidas = (List<Partida2>) model.getAttribute("partidas");
        assertNotNull(partidas);
        assertEquals(3, partidas.size());
        assertTrue(partidas.stream().allMatch(p -> p.getEstado() == Estado.EN_ESPERA));
    }


    @Test
    public void queSeMuestreElNombreDelUsuarioEnElLobby() {

        Model model = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);

        Jugador jugadorMock = new Jugador();
        jugadorMock.setNombre("july3p");

        when(session.getAttribute("jugador")).thenReturn(jugadorMock);

        ModelAndView mav = controladorLobby.Lobby(session, model);

        assertEquals("lobby", mav.getViewName());
        assertTrue(model.containsAttribute("jugador"));

        Jugador jugador = (Jugador) model.getAttribute("jugador");
        assert jugador != null;
        assertEquals("july3p", jugador.getNombre());
    }




}
