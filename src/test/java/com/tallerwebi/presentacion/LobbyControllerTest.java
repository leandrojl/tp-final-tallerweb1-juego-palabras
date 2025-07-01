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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LobbyControllerTest {
    private PartidaService partidaServiceMock;
    private LobbyService lobbyServiceMock;
    private UsuarioService usuarioServiceMock;
    private UsuarioPartidaService usuarioPartidaServiceMock;
    private LobbyController lobbyController;
    private HttpSession sessionMock;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {

        partidaServiceMock = Mockito.mock(PartidaService.class);
        lobbyServiceMock = Mockito.mock(LobbyService.class);
        usuarioServiceMock = Mockito.mock(UsuarioService.class);
        usuarioPartidaServiceMock = Mockito.mock(UsuarioPartidaService.class);
        sessionMock = Mockito.mock(HttpSession.class);

        lobbyController = new LobbyController(
                partidaServiceMock,
                lobbyServiceMock,
                usuarioServiceMock,
                usuarioPartidaServiceMock);

//        Este bloque configura Thymeleaf para que los tests de MockMvc puedan
//        renderizar las vistas HTML reales desde el sistema de archivos,
//        usando los templates ubicados en src/main/webapp/WEB-INF/views/thymeleaf/.
//        Así, cuando pruebas controladores que devuelven vistas, MockMvc puede procesar
//        y verificar el contenido HTML generado, igual que en la aplicación real.
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
    public void deberiaRedirigirASalaDeEsperaTrasCrearSala() throws Exception {
        Long usuarioId = 1L;
        Serializable idPartida = 10L;
        Mockito.when(partidaServiceMock.crearPartida(Mockito.any(Partida.class))).thenReturn(idPartida);

        mockMvc.perform(
                        post("/crear-sala")
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
        Mockito.when(partidaServiceMock.crearPartida(Mockito.any(Partida.class))).thenReturn(idPartida);

        mockMvc.perform(
                post("/crear-sala")
                        .param("nombre", "Sala Test 2")
                        .param("idioma", "Inglés")
                        .param("permiteComodin", "false")
                        .param("rondasTotales", "3")
                        .param("maximoJugadores", "6")
                        .param("minimoJugadores", "3")
                        .sessionAttr("usuarioId", usuarioId)
        );

        Mockito.verify(partidaServiceMock).crearPartida(Mockito.argThat(partida ->
                partida.getNombre().equals("Sala Test 2") &&
                        partida.getIdioma().equals("Inglés") &&
                        !partida.isPermiteComodin() &&
                        partida.getRondasTotales() == 3 &&
                        partida.getMaximoJugadores() == 6 &&
                        partida.getMinimoJugadores() == 3 &&
                        partida.getEstado() == Estado.EN_ESPERA
        ));
    }

    @Test
    public void deberiaAgregarUsuarioAPartidaConValoresIniciales() throws Exception {
        Long usuarioId = 3L;
        Serializable idPartida = 30L;
        Mockito.when(partidaServiceMock.crearPartida(Mockito.any(Partida.class))).thenReturn(idPartida);

        mockMvc.perform(
                post("/crear-sala")
                        .param("nombre", "Sala Test 3")
                        .param("idioma", "Francés")
                        .param("permiteComodin", "true")
                        .param("rondasTotales", "4")
                        .param("maximoJugadores", "5")
                        .param("minimoJugadores", "2")
                        .sessionAttr("usuarioId", usuarioId)
        );

        Mockito.verify(usuarioPartidaServiceMock).agregarUsuarioAPartida(usuarioId, (Long) idPartida, 0, false, Estado.EN_ESPERA);
    }





    @Test
    public void deberiaMostrarBotonCrearSalaEnLobby() throws Exception {
        mockMvc.perform(get("/lobby"))
                .andExpect(content().string(containsString("Crear Sala")));
    }

    @Test
    public void cuandoCreoUnaSalaParaUnaPartidaSeRedirigeALaSalaDeEspera() throws Exception {
        String nombre = "Sala Test";
        String idioma = "Español";
        boolean permiteComodin = true;
        int rondasTotales = 5;
        int maximoJugadores = 4;
        int minimoJugadores = 2;
        Long usuarioId = 1L;
        Serializable idPartida = 10L;

        when(sessionMock.getAttribute("usuarioId")).thenReturn(usuarioId);

        Partida nuevaPartida = new Partida(
                nombre,
                idioma,
                permiteComodin,
                rondasTotales,
                maximoJugadores,
                minimoJugadores,
                Estado.EN_ESPERA);

        Mockito.when(partidaServiceMock.crearPartida(nuevaPartida)).thenReturn(idPartida);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(lobbyController).build();

        mockMvc.perform(
                        post("/crear-sala")
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



        doNothing().when(usuarioPartidaServiceMock).agregarUsuarioAPartida(usuarioId, (Long) idPartida, 0, false, Estado.EN_ESPERA);

        Mockito.verify(partidaServiceMock).crearPartida(Mockito.any(Partida.class));
        Mockito.verify(usuarioPartidaServiceMock).agregarUsuarioAPartida(usuarioId, (Long) idPartida, 0, false, Estado.EN_ESPERA);
    }



}



