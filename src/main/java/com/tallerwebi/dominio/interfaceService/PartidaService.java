package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.DefinicionDto;
import com.tallerwebi.dominio.DtoIntento;
import com.tallerwebi.dominio.ResultadoIntentoDto;
import com.tallerwebi.dominio.model.Partida;


public interface PartidaService {
    Partida iniciarNuevaPartida(String jugadorId, String nombre);
    Partida obtenerPartida(String jugadorId);
    void eliminarPartida(String jugadorId);


    void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje);

    ResultadoIntentoDto procesarIntento(DtoIntento intento, String name);

    DefinicionDto iniciarPrimerRonda(Long partidaId);
}

