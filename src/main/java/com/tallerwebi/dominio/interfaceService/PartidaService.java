package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.DtoIntento;
import com.tallerwebi.dominio.RondaDto;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;

import java.io.Serializable;


public interface PartidaService {
    Partida iniciarNuevaPartida(String jugadorId, String nombre);
    Partida obtenerPartida(String jugadorId);
    void eliminarPartida(String jugadorId);


    void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje);

    void procesarIntento(DtoIntento intento);

    RondaDto iniciarNuevaRonda(Long partidaId);
    Serializable crearPartida (Partida2 nuevaPartida);

    RondaDto  obtenerPalabraYDefinicionDeRondaActual(Long partidaId);
}

