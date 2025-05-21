package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Jugador;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControladorLobbyTest {




    @Test
    public void queSeMuestreElNombreDelUsuarioEnElLobby() {

        Model model = new ExtendedModelMap();
        HttpSession session = mock(HttpSession.class);
        Jugador jugadorMock = new Jugador();
        jugadorMock.setNombre("july3p");
        when(session.getAttribute("jugador")).thenReturn(jugadorMock);
        ControladorLobby controladorLobby = new ControladorLobby();
        ModelAndView mav = controladorLobby.Lobby(session, model);
        assertEquals("lobby", mav.getViewName());
        assertTrue(model.containsAttribute("jugador"));

        Jugador jugador = (Jugador) model.getAttribute("jugador");
        assert jugador != null;
        assertEquals("july3p", jugador.getNombre());
    }




}
