package com.tallerwebi.dominio.interfaceService;


import com.tallerwebi.dominio.DTO.*;
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

    Serializable crearPartida(CrearPartidaDTO partidaDTO, Long creadorId);

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


    void cancelarPartidaSiEsCreador(Long idUsuario, Long idPartida);

    String obtenerNombrePartidaPorId(Long idPartida);


    void activarComodin(Long idPartida, Long idUsuario, String nombreUsuario, boolean letraRepetida);

    void obtenerUsuariosParaBloquear(Long idPartida, String nombreUsuario);

    void bloquearUsuario(Long idPartida, Long idUsuario, String nombreUsuario, String usuarioABloquear);

    void abandonarPartida(Long idUsuario, Long idPartida, String nombreUsuario);

    void mostrarRanking(MensajeRecibidoDTO mensaje);
}

