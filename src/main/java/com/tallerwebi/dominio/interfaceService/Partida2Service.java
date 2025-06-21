package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.DefinicionDto;
import com.tallerwebi.dominio.DtoIntento;
import com.tallerwebi.dominio.ResultadoIntentoDto;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;


public interface Partida2Service {

   // void crearPartida(Partida2 nuevaPartida);

    Partida iniciarNuevaPartida(String jugadorId, String nombre);

    Partida obtenerPartida(String jugadorId);

    void eliminarPartida(String jugadorId);

    void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje);

    ResultadoIntentoDto procesarIntento(DtoIntento intento, String nombreJugador);

    DefinicionDto iniciarPrimerRonda(Long partidaId);


    void enviarDatosDeLaRonda(DefinicionDto datosRonda, Long partidaId);
}

