package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceRepository.UsuarioPartidaRepository;

import com.tallerwebi.dominio.interfaceRepository.UsuarioRepository;
import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
import com.tallerwebi.dominio.interfaceService.UsuarioService;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Usuario;

import com.tallerwebi.dominio.interfaceService.UsuarioPartidaService;
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

    public void agregarUsuarioAPartida(Long idUsuario, Long idPartida, int puntaje, boolean gano, Estado estado) {
        usuarioPartidaRepository.agregarUsuarioAPartida(idUsuario, idPartida, puntaje, gano, estado);
    }

    @Override
    public String obtenerNombreDeUsuarioEnLaPartida(Long usuarioId, Long idPartida) {
        return usuarioPartidaRepository.obtenerNombreDeUsuarioEnLaPartida(usuarioId, idPartida);
    }

    @Override
    public void asociarUsuarioConPartida(Usuario usuario, Partida partida) {

        UsuarioPartida usuarioPartida = generarUsuarioPartida(usuario, partida);
        usuarioPartidaRepository.guardarUsuarioPartida(usuarioPartida);

    }

    private static UsuarioPartida generarUsuarioPartida(Usuario usuario, Partida partida) {
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
    public List<UsuarioPartida> obtenerPorPartida(Long partidaId) {
        return List.of();
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

    @Override
    public UsuarioPartida buscarUsuarioPartida(Long idPartida, Long usuarioId) {
        return usuarioPartidaRepository.obtenerUsuarioPartida(idPartida,usuarioId);
    }//HACER TEST A ESTE

    @Override
    public void sumarPuntos(Long usuarioId, Long partidaId, int puntos) {
        usuarioPartidaRepository.sumarPuntaje(usuarioId, partidaId,puntos);
    }

    @Override
    public Usuario obtenerUsuarioPorUsuarioIdYPartidaId(Long usuarioId, Long partidaId) {
        return usuarioPartidaRepository.obtenerUsuarioPorUsuarioIdYPartidaId(usuarioId, partidaId);
    }


}
