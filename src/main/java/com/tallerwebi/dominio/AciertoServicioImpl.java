package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceService.AciertoService;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.infraestructura.AciertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AciertoServicioImpl implements AciertoService {


    private final AciertoRepository aciertoRepositorio;
    private final UsuarioPartidaRepository UsuarioPartidaRepositorio;

    @Autowired
    public AciertoServicioImpl(AciertoRepository aciertoRepositorio, UsuarioPartidaRepository usuarioPartidaRepositorio) {
        this.aciertoRepositorio = aciertoRepositorio;
        UsuarioPartidaRepositorio = usuarioPartidaRepositorio;
    }

    @Override
    public boolean todosAcertaron(Long idPartida, Long idRonda) {
        long totalUsuarios = UsuarioPartidaRepositorio.contarUsuariosEnPartida(idPartida);
        long aciertos = aciertoRepositorio.contarUsuariosQueAcertaron(idRonda);
        return aciertos >= totalUsuarios;
    }


}
