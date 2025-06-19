package com.tallerwebi.dominio.interfaceService;

import java.util.List;
import java.util.Map;

public interface SalaDeEsperaService {


    Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros);

    List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores);


    void irAlJuego();

    void notificarQueSeUneUnNuevoUsuarioALaSala(String nombreUsuarioQueAcabaDeUnirseALaSala);
}
