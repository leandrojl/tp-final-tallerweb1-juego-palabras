package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.model.Partida;

import java.io.Serializable;
import java.util.List;


public interface PartidaService {

    void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje);

    ResultadoIntentoDto procesarIntento(DtoIntento intento, String name);

    DefinicionDto iniciarNuevaRonda(Long partidaId);

    DefinicionDto avanzarRonda(MensajeAvanzarRondaDTO dto);

    boolean esUltimaRonda(Long idPartida);

    Serializable crearPartida (Partida nuevaPartida);

    void enviarRankingFinal(Long idPartida);

    List<RankingDTO> obtenerRanking(Long partidaId);
}

