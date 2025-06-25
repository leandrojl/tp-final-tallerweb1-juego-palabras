package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Usuario;

import java.io.Serializable;
import java.util.List;

public interface PartidaRepository {
    Partida2 buscarPorId(Long id);

    // Ejemplo: guardar una partida (por si queres usarlo despues)
    void guardar(Partida2 partida);

    Serializable crearPartida(Partida2 nuevaPartida);

}
