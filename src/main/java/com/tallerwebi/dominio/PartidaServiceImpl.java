package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import com.tallerwebi.infraestructura.PartidaRepository;
import com.tallerwebi.infraestructura.UsuarioPartidaRepository;
import com.tallerwebi.infraestructura.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PartidaServiceImpl implements PartidaService {
    private final PartidaRepository partidaRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioPartidaRepository usuarioPartidaRepository;

    @Autowired
    public PartidaServiceImpl(PartidaRepository partidaRepository,
                              UsuarioRepository usuarioRepository,
                              UsuarioPartidaRepository usuarioPartidaRepository) {
        this.partidaRepository = partidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioPartidaRepository = usuarioPartidaRepository;
    }

    @Override
    @Transactional
    public Partida2 iniciarNuevaPartida(String nombrePartida) {
        // Crear la nueva partida
        Partida2 partida = new Partida2();
        partida.setNombre(nombrePartida);
        partida.setEstado(Estado.EN_ESPERA);
        partidaRepository.guardar(partida);

        return partida;
    }

    @Override
    public boolean estaTerminada(Partida2 partida) {
        return partida.getEstado() == Estado.FINALIZADA;
    }

    @Override
    public Partida2 buscarPorId(Long partidaId) {
        return partidaRepository.buscarPorId(partidaId);
    }

    @Override
    public void actualizarEstado(Long id, Estado estado) {
        this.partidaRepository.actualizarEstado(id, estado);
    }


}
