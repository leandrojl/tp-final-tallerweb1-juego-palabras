package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Usuario;

import java.util.List;
import java.util.Map;

public interface SalaDeEsperaService {


    Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros) throws NoHayJugadoresEnLaSalaDeEsperaException;

    List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores);


}
