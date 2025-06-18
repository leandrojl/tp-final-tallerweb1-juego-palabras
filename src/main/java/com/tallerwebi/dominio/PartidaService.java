package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;

import java.util.List;


public interface PartidaService {
    Partida iniciarNuevaPartida(String jugadorId, String nombre);
    Partida obtenerPartida(String jugadorId);
    void eliminarPartida(String jugadorId);


    void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje);

}

