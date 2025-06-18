package com.tallerwebi.dominio;


import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Service
public class RankingServiceImpl implements RankingService {

   private final UsuarioPartidaRepository usuarioPartidaRepository;

@Autowired
    public RankingServiceImpl(UsuarioPartidaRepository usuarioPartidaRepository) {
        this.usuarioPartidaRepository = usuarioPartidaRepository;
    }

    public List<Object[]> obtenerRanking() {
    return this.usuarioPartidaRepository.obtenerRanking();
    }
}
