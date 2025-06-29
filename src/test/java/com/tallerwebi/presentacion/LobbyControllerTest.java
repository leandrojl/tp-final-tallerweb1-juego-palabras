package com.tallerwebi.presentacion;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.*;
import com.tallerwebi.dominio.model.Partida;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.http.HttpSession;

import java.io.Serializable;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LobbyControllerTest {


    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        PartidaService partidaService = Mockito.mock(PartidaService.class);
        LobbyService lobbyService = Mockito.mock(LobbyService.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
        UsuarioPartidaService usuarioPartidaService = Mockito.mock(UsuarioPartidaService.class);
        LobbyController lobbyController = new LobbyController(partidaService, lobbyService, usuarioService, usuarioPartidaService);

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
    public void deberiaCrearSalaYRedirigirASalaDeEspera() throws Exception {

        Long usuarioId = 1L;
        Serializable idPartida = 10L;


        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute("usuarioId")).thenReturn(usuarioId);


        PartidaService partidaService = Mockito.mock(PartidaService.class);
        LobbyService lobbyService = Mockito.mock(LobbyService.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
        UsuarioPartidaService usuarioPartidaService = Mockito.mock(UsuarioPartidaService.class);


        LobbyController lobbyController = new LobbyController(partidaService, lobbyService, usuarioService, usuarioPartidaService);


        Mockito.when(partidaService.crearPartida(Mockito.any(Partida.class))).thenReturn(idPartida);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(lobbyController).build();

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/crear-sala")
                                .param("nombre", "Sala Test")
                                .param("idioma", "Español")
                                .param("permiteComodin", "true")
                                .param("rondasTotales", "5")
                                .param("maximoJugadores", "4")
                                .param("minimoJugadores", "2")
                                .sessionAttr("usuarioId", usuarioId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sala-de-espera"));


        Mockito.verify(partidaService).crearPartida(Mockito.any(Partida.class));
        Mockito.verify(usuarioPartidaService).agregarUsuarioAPartida(usuarioId, (Long) idPartida, 0, false, Estado.EN_ESPERA);
    }


    @Test
    public void deberiaRedirigirASalaDeEsperaTrasCrearSala() throws Exception {
        Long usuarioId = 1L;
        Serializable idPartida = 10L;

        PartidaService partidaService = Mockito.mock(PartidaService.class);
        LobbyService lobbyService = Mockito.mock(LobbyService.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
        UsuarioPartidaService usuarioPartidaService = Mockito.mock(UsuarioPartidaService.class);

        Mockito.when(partidaService.crearPartida(Mockito.any(Partida.class))).thenReturn(idPartida);

        LobbyController lobbyController = new LobbyController(partidaService, lobbyService, usuarioService, usuarioPartidaService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(lobbyController).build();

        mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/crear-sala")
                                .param("nombre", "Sala Test")
                                .param("idioma", "Español")
                                .param("permiteComodin", "true")
                                .param("rondasTotales", "5")
                                .param("maximoJugadores", "4")
                                .param("minimoJugadores", "2")
                                .sessionAttr("usuarioId", usuarioId)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sala-de-espera"));
    }


    @Test
    public void deberiaLlamarCrearPartidaConDatosCorrectos() throws Exception {
        Long usuarioId = 2L;
        Serializable idPartida = 20L;

        PartidaService partida2Service = Mockito.mock(PartidaService.class);
        LobbyService lobbyService = Mockito.mock(LobbyService.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
        UsuarioPartidaService usuarioPartidaService = Mockito.mock(UsuarioPartidaService.class);

        Mockito.when(partida2Service.crearPartida(Mockito.any(Partida.class))).thenReturn(idPartida);

        LobbyController lobbyController = new LobbyController(partida2Service, lobbyService, usuarioService, usuarioPartidaService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(lobbyController).build();

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/crear-sala")
                        .param("nombre", "Sala Test 2")
                        .param("idioma", "Inglés")
                        .param("permiteComodin", "false")
                        .param("rondasTotales", "3")
                        .param("maximoJugadores", "6")
                        .param("minimoJugadores", "3")
                        .sessionAttr("usuarioId", usuarioId)
        );

        Mockito.verify(partida2Service).crearPartida(Mockito.argThat(partida ->
                partida.getNombre().equals("Sala Test 2") &&
                        partida.getIdioma().equals("Inglés") &&
                        !partida.isPermiteComodin() &&
                        partida.getRondasTotales() == 3 &&
                        partida.getMaximoJugadores() == 6 &&
                        partida.getMinimoJugadores() == 3 &&
                        partida.getEstado() == Estado.EN_ESPERA
        ));
    }

    // Test: Se agrega el usuario a la partida correctamente
    @Test
    public void deberiaAgregarUsuarioAPartidaConValoresIniciales() throws Exception {
        Long usuarioId = 3L;
        Serializable idPartida = 30L;

        PartidaService partidaService = Mockito.mock(PartidaService.class);
        LobbyService lobbyService = Mockito.mock(LobbyService.class);
        UsuarioService usuarioService = Mockito.mock(UsuarioService.class);
        UsuarioPartidaService usuarioPartidaService = Mockito.mock(UsuarioPartidaService.class);

        Mockito.when(partidaService.crearPartida(Mockito.any(Partida.class))).thenReturn(idPartida);

        LobbyController lobbyController = new LobbyController(partidaService, lobbyService, usuarioService, usuarioPartidaService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(lobbyController).build();

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/crear-sala")
                        .param("nombre", "Sala Test 3")
                        .param("idioma", "Francés")
                        .param("permiteComodin", "true")
                        .param("rondasTotales", "4")
                        .param("maximoJugadores", "5")
                        .param("minimoJugadores", "2")
                        .sessionAttr("usuarioId", usuarioId)
        );

        Mockito.verify(usuarioPartidaService).agregarUsuarioAPartida(usuarioId, (Long) idPartida, 0, false, Estado.EN_ESPERA);
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






}