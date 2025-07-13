package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.DTO.EstadoJugadorDTO;
import com.tallerwebi.dominio.DTO.MensajeDto;
import com.tallerwebi.dominio.DTO.MensajeRecibidoDTO;

import java.util.List;
import java.util.Map;

public interface SalaDeEsperaService {


    Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros);

    List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores);


    void mostrarAUnUsuarioLosUsuariosExistentesEnSala(String nombreUsuarioQueAcabaDeUnirseALaSala, Long idPartida);

    void redireccionarUsuariosAPartida(MensajeRecibidoDTO mensajeRecibidoDTO);

    void actualizarElEstadoDeUnUsuario(EstadoJugadorDTO estadoJugadorDTO, String nombreUsuarioDelPrincipal);

    MensajeRecibidoDTO abandonarSala(MensajeDto mensaje,String nombreUsuarioDelPrincipal);

    void expulsarJugador(MensajeDto mensajeDto, String nombreUsuarioDelPrincipal);

    void enviarMensajeDeChat(MensajeDto mensajeDto, String nombreUsuario);
}
