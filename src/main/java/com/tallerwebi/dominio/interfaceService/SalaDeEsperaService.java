package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.MensajeRecibidoDTO;

import java.util.List;
import java.util.Map;

public interface SalaDeEsperaService {


    Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros);

    List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores);


    void irAlJuego();

    void mostrarAUnUsuarioLosUsuariosExistentesEnSala(String nombreUsuarioQueAcabaDeUnirseALaSala);

    void redireccionarUsuariosAPartida(MensajeRecibidoDTO mensajeRecibidoDTO);
}
