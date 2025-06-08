package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.PartidaService;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida;
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

    PartidaService partidaServiceMock = mock(PartidaService.class);
    LobbyController controladorLobby = new LobbyController(partidaServiceMock);

    @Test
    public void queSePuedanVerPartidasEnElLobby() {

       //dado que tengo un modelo que va a estar en la vista lobby
        Model model = new ExtendedModelMap();

        //dado que tengo una session mockeada
        HttpSession session = mock(HttpSession.class);

        //dado que tengo un jugador mockeado
        when(session.getAttribute("usuario")).thenReturn(new Jugador("july3p"));


        //dado que tengo una lista de partidas mockeada
        List<Partida> partidasMock = List.of(new Partida(), new Partida(), new Partida());

        //cuando agrego las partidas al modelo
        model.addAttribute("partidas", partidasMock);

        //cuando llamo al metodo lobby
        ModelAndView mav = controladorLobby.Lobby(session, model);

        //entonces espero que el nombre de la vista sea "lobby"
        assertEquals("lobby", mav.getViewName());

        //y que el modelo contenga el atributo "partidas"
        assertTrue(model.containsAttribute("partidas"));


    }


    @Test
    public void queSeMuestreElNombreDelUsuarioEnElLobby() {

        Model model = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);
        Jugador jugadorMock = new Jugador();
        jugadorMock.setNombre("july3p");
        when(session.getAttribute("jugador")).thenReturn(jugadorMock);
        LobbyController controladorLobby = new LobbyController(partidaServiceMock);
        ModelAndView mav = controladorLobby.Lobby(session, model);
        assertEquals("lobby", mav.getViewName());
        assertTrue(model.containsAttribute("jugador"));

        Jugador jugador = (Jugador) model.getAttribute("jugador");
        assert jugador != null;
        assertEquals("july3p", jugador.getNombre());
    }




}
