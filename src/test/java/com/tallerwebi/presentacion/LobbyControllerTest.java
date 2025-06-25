package com.tallerwebi.presentacion;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.interfaceService.Partida2Service;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import com.tallerwebi.dominio.model.Jugador;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.http.HttpSession;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LobbyControllerTest {

    PartidaService partida2ServiceMock;
    LobbyService lobbyService;
    LobbyController controladorLobby;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        Partida2Service partida2Service = Mockito.mock(Partida2Service.class);
        LobbyService lobbyService = Mockito.mock(LobbyService.class);
        LobbyController lobbyController = new LobbyController(partida2Service, lobbyService);

        // Usar FileTemplateResolver para leer desde el sistema de archivos
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix("src/main/webapp/WEB-INF/views/thymeleaf/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine);
        viewResolver.setCharacterEncoding("UTF-8");

        mockMvc = MockMvcBuilders.standaloneSetup(lobbyController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void deberiaRetornarVistaCrearSalaAlAccederACrearSala() throws Exception {
        mockMvc.perform(get("/crear-sala"))
                .andExpect(status().isOk())
                .andExpect(view().name("crear-sala"));
    }

    @Test
    public void deberiaMostrarFormularioDeConfiguracionDePartidaAlCrearSala() throws Exception {
        mockMvc.perform(get("/crear-sala"))
                .andExpect(content().string(containsString("form"))) // El formulario
                .andExpect(content().string(containsString("name=\"idioma\"")))
                .andExpect(content().string(containsString("name=\"permiteComodin\"")))
                .andExpect(content().string(containsString("name=\"rondasTotales\"")))
                .andExpect(content().string(containsString("name=\"maximoJugadores\"")))
                .andExpect(content().string(containsString("name=\"minimoJugadores\"")));
    }

    @Test
    public void deberiaMostrarBotonCrearSalaEnLobby() throws Exception {
        mockMvc.perform(get("/lobby"))
                .andExpect(content().string(containsString("Crear Sala")));
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