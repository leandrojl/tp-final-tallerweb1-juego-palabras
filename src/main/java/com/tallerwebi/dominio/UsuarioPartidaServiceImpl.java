package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;
import com.tallerwebi.dominio.model.UsuarioPartida;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UsuarioPartidaServiceImpl implements UsuarioPartidaService {

    @Autowired
    private final UsuarioPartidaRepository usuarioPartidaRepository;


    @Autowired
    public UsuarioPartidaServiceImpl(UsuarioPartidaRepository usuarioPartidaRepository) {
        this.usuarioPartidaRepository = usuarioPartidaRepository;
    }


    @Override
    public void asociarUsuarioConPartida(Usuario usuario, Partida2 partida) {

        UsuarioPartida usuarioPartida = generarUsuarioPartida(usuario, partida);
        usuarioPartidaRepository.guardarUsuarioPartida(usuarioPartida);

    }

    private static UsuarioPartida generarUsuarioPartida(Usuario usuario, Partida2 partida) {
        UsuarioPartida up = new UsuarioPartida();
        up.setUsuario(usuario);
        up.setPartida(partida);
        up.setPuntaje(0);
        up.setGano(false);
        return up;
    }

    @Override
    public void actualizarPuntaje(Long usuarioId, Long partidaId, int nuevoPuntaje) {
        usuarioPartidaRepository.actualizarPuntaje(usuarioId,partidaId,nuevoPuntaje);
    }

    @Override
    public int obtenerPuntaje(Long usuarioId, Long partidaId) {
        return usuarioPartidaRepository.obtenerPuntaje(usuarioId,partidaId);
    }



    @Override
    public void marcarComoPerdedor(Long usuarioId, Long partidaId) {
        UsuarioPartida relacion = usuarioPartidaRepository.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId);
        if (relacion != null) {
            relacion.setGano(false);
            relacion.setEstado(Estado.FINALIZADA);

            usuarioPartidaRepository.actualizar(relacion);
        }
    }

    @Override
    public UsuarioPartida obtenerUsuarioEspecificoPorPartida(Long usuarioId, Long partidaId) {
        return usuarioPartidaRepository.obtenerUsuarioEspecificoPorPartida(usuarioId, partidaId);
    }


}

