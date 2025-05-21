package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.NoHayJugadoresEnLaSalaDeEsperaException;

import java.util.List;
import java.util.Map;

public interface ServicioSalaDeEspera {


    Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros) throws NoHayJugadoresEnLaSalaDeEsperaException;

    List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores);

    List<Usuario> crearUsuariosParaQueNoSeRompaLaVistaJuego();
}
