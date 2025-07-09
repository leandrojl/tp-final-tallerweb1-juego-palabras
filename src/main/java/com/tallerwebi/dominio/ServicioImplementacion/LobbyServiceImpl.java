package com.tallerwebi.dominio.ServicioImplementacion;

import com.tallerwebi.dominio.excepcion.PartidaAleatoriaNoDisponibleException;
import com.tallerwebi.dominio.interfaceRepository.LobbyRepository;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.dominio.interfaceService.LobbyService;
import com.tallerwebi.dominio.model.Partida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Transactional
@Service
public class LobbyServiceImpl implements LobbyService {

    private final LobbyRepository lobbyRepository;
    private PartidaRepository partidaRepo;

    @Autowired
    public LobbyServiceImpl(LobbyRepository lobbyRepository, PartidaRepository partidaRepo) {
        this.lobbyRepository = lobbyRepository;
        this.partidaRepo = partidaRepo;
    }

    @Override
    public List<Partida> obtenerPartidasEnEspera() {
        return lobbyRepository.obtenerPartidasEnEspera();
    }

    @Override
    public void guardar(Partida partida) {
        lobbyRepository.guardar(partida);
    }

    @Override
    public List<Partida> buscarPartidasPorNombre(String nombre) {
        return lobbyRepository.obtenerPartidasPorNombre(nombre);
    }

    @Override
    public Long obtenerUnaPartidaAleatoria() {
        Partida partida = partidaRepo.obtenerPartidaAleatoria();
        if(partida == null){
            throw new PartidaAleatoriaNoDisponibleException("No hay partidas disponibles en este momento");
        }
        return partida.getId();
    }
}