package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LobbyServiceImpl implements LobbyService {

    private final LobbyRepository lobbyRepository;

    @Autowired
    public LobbyServiceImpl(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    @Override
    public List<Partida2> obtenerPartidasEnEspera() {
        return lobbyRepository.obtenerPartidasEnEspera();
    }

    @Override
    public void guardar(Partida2 partida) {
        lobbyRepository.guardar(partida);
    }
}