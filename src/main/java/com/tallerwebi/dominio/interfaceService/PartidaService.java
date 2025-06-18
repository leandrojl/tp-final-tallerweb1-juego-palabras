package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.model.Partida;


public interface PartidaService {
    Partida iniciarNuevaPartida(String jugadorId, String nombre);
    Partida obtenerPartida(String jugadorId);
    void eliminarPartida(String jugadorId);


    void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje);

}

