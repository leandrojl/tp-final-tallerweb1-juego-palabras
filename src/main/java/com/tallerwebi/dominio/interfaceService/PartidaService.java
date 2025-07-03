package com.tallerwebi.dominio.interfaceService;


import com.tallerwebi.dominio.DTO.DefinicionDto;
import com.tallerwebi.dominio.DTO.DtoIntento;
import com.tallerwebi.dominio.DTO.MensajeAvanzarRondaDTO;
import com.tallerwebi.dominio.DTO.RankingDTO;
import com.tallerwebi.dominio.DTO.RondaDto;
import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Ronda;

import java.io.Serializable;
import java.util.List;


public interface PartidaService {

    Partida obtenerPartidaPorId(Long partidaId);

    void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje);

    void procesarIntento(DtoIntento intento, String nombre);

    DefinicionDto avanzarRonda(MensajeAvanzarRondaDTO dto);

    boolean esUltimaRonda(Long idPartida);

    Serializable crearPartida (Partida nuevaPartida);

    void enviarRankingFinal(Long idPartida);

    List<RankingDTO> obtenerRanking(Long partidaId);

    void procesarIntento1(DtoIntento intento);

    RondaDto iniciarNuevaRonda(Long partidaId);

    RondaDto  obtenerPalabraYDefinicionDeRondaActual(Long partidaId);

    void finalizarRonda(Ronda ronda);

    void cancelarPartidaDeUsuario(Long idUsuario, Long idPartida);

    boolean verificarSiEsElCreadorDePartida(Long idUsuario, Long idPartida);

    Estado verificarEstadoDeLaPartida(Long idPartida);

    RondaDto construirDtoDesdeRondaExistente(Ronda ronda, Long partidaId);

}

