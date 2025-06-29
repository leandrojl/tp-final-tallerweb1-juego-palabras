package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.EstadoJugadorDTO;
import com.tallerwebi.dominio.model.MensajeDto;
import com.tallerwebi.dominio.model.MensajeRecibidoDTO;

import java.util.List;
import java.util.Map;

public interface SalaDeEsperaService {


    Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros);

    List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores);


    void mostrarAUnUsuarioLosUsuariosExistentesEnSala(String nombreUsuarioQueAcabaDeUnirseALaSala, Long idPartida);

    Boolean redireccionarUsuariosAPartida(MensajeRecibidoDTO mensajeRecibidoDTO);

    Boolean actualizarElEstadoDeUnUsuario(EstadoJugadorDTO estadoJugadorDTO, String nombreUsuarioDelPrincipal);

    MensajeRecibidoDTO abandonarSala(MensajeDto mensaje,String nombreUsuarioDelPrincipal);
}
