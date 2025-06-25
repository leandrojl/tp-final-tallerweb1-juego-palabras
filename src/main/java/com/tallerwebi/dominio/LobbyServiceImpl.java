package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceRepository.LobbyRepository;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.model.Partida2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Transactional
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

    @Override
    public List<Partida2> buscarPartidasPorNombre(String nombre) {
        return lobbyRepository.obtenerPartidasPorNombre(nombre);
    }
}