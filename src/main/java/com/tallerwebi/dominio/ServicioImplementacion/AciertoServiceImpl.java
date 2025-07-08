package com.tallerwebi.dominio.ServicioImplementacion;

import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.AciertoService;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceRepository.AciertoRepository;
import com.tallerwebi.dominio.model.Acierto;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.interfaceRepository.RondaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AciertoServiceImpl implements AciertoService {


    private final AciertoRepository aciertoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RondaRepository rondaRepository;
    private final UsuarioPartidaRepository usuarioPartidaRepository;

    @Autowired
    public AciertoServiceImpl(AciertoRepository aciertoRepository,
                              UsuarioPartidaRepository usuarioPartidaRepository,
                              UsuarioRepository usuarioRepository,
                              RondaRepository rondaRepository) {
        this.aciertoRepository = aciertoRepository;
        this.usuarioPartidaRepository = usuarioPartidaRepository;
        this.usuarioRepository = usuarioRepository;
        this.rondaRepository = rondaRepository;
    }


    @Override
    public boolean todosAcertaron(Long idPartida, Long idRonda) {
        long totalUsuarios = usuarioPartidaRepository.contarUsuariosEnPartida(idPartida);
        long aciertos = aciertoRepository.contarUsuariosQueAcertaron(idRonda);
        return aciertos >= totalUsuarios;
    }

    @Override
    public boolean jugadorYaAcerto(Long usuarioId, Long rondaId) {
        return aciertoRepository.jugadorYaAcerto(usuarioId, rondaId);
    }

    @Override
    public int registrarAcierto(Long usuarioId, Long rondaId) {
        if (jugadorYaAcerto(usuarioId, rondaId)) {
            return 0;
        }

        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);
        if (usuario==null){
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId);
        }

        Ronda ronda = rondaRepository.buscarPorId(rondaId);
        if (ronda==null){
            throw new IllegalArgumentException("Ronda no encontrada con ID: " + rondaId);
        }

        int cantidadAciertos = aciertoRepository.contarAciertosPorRondaId(rondaId);

        Acierto acierto = new Acierto();
        acierto.setUsuario(usuario);
        acierto.setRonda(ronda);
        acierto.setOrdenDeAcierto(cantidadAciertos + 1);

        aciertoRepository.registrarAcierto(acierto);

        int puntos;
        switch (acierto.getOrdenDeAcierto()) {
            case 1: puntos = 10; break;
            case 2: puntos = 7; break;
            case 3: puntos = 5; break;
            default: puntos = 3; break;
        }

        return puntos;
    }


}
