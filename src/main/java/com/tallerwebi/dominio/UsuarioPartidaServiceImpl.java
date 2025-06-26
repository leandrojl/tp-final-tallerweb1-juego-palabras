package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioPartidaServiceImpl implements UsuarioPartidaService {

    private final UsuarioPartidaRepository usuarioPartidaRepository;

    @Autowired
    public UsuarioPartidaServiceImpl(UsuarioPartidaRepository usuarioPartidaRepository) {
        this.usuarioPartidaRepository = usuarioPartidaRepository;
    }


    @Override
    public void agregarUsuarioAPartida(Long idUsuario, Long idPartida, int puntaje, boolean gano, Estado estado) {
        usuarioPartidaRepository.agregarUsuarioAPartida(idUsuario, idPartida, puntaje, gano, estado);
    }
}