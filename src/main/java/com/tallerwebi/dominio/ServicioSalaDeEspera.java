package com.tallerwebi.dominio;

import java.util.List;
import java.util.Map;

public interface ServicioSalaDeEspera {


    Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros);

    List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores);

    List<Usuario> crearUsuariosParaQueNoSeRompaLaVistaJuego();
}
