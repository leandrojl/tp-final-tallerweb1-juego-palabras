package com.tallerwebi.dominio.interfaceService;

import com.tallerwebi.dominio.DefinicionDto;
import com.tallerwebi.dominio.model.Partida2;

import java.io.Serializable;


public interface Partida2Service {

    DefinicionDto iniciarPrimerRonda(Long partidaId);
     Serializable crearPartida (Partida2 nuevaPartida);
}

