package com.tallerwebi.config;

import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.interfaceService.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DatabaseInitializationConfig {

    private final LobbyService lobbyService;
    private final RegistroService registroService;

    @Autowired
    public DatabaseInitializationConfig(LobbyService lobbyService, RegistroService registroService) {
        this.lobbyService = lobbyService;
        this.registroService = registroService;
    }

    @PostConstruct
    public void init() {
        if (lobbyService.obtenerPartidasEnEspera().isEmpty()) {
            //lobbyService.guardar(new Partida2("Partida en espera 1", "Ingles", true, 5, 5, 2, Estado.EN_ESPERA));
            //lobbyService.guardar(new Partida2("Partida en espera 2", "Ingles", false, 3, 5, 4, Estado.EN_ESPERA));
            //lobbyService.guardar(new Partida2("Partida en espera 3", "Ingles", true, 7, 5, 3, Estado.EN_ESPERA));
            //lobbyService.guardar(new Partida2("Partida EN CURSO 1", "Ingles", true, 7, 5, 3, Estado.EN_CURSO));
            //lobbyService.guardar(new Partida2("Partida FINALIZADA", "Ingles", true, 7, 5, 3, Estado.FINALIZADA));
        }

        if(registroService.obtenerUsuariosLogueados().isEmpty()){
            registroService.registrar("pepe","12345678");
            registroService.registrar("jose","12345678");
            registroService.registrar("lucas","12345678");
        }
    }
}