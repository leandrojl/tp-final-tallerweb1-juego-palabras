package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.NoHayJugadoresEnLaSalaDeEsperaException;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("servicioSalaDeEspera")
@Transactional
public class ServicioSalaDeEsperaImpl implements ServicioSalaDeEspera {


    @Override
    public Map<Long, Boolean> obtenerJugadoresDelFormulario(Map<String, String> parametros)  {
        Map<Long, Boolean> jugadores = new HashMap<>();

        parametros.forEach((clave, valor) -> {
            if (clave.startsWith("jugador_")) {
                Long jugadorId = Long.parseLong(clave.replace("jugador_", ""));
                Boolean listo = Boolean.parseBoolean(valor);
                jugadores.put(jugadorId, listo);
            }
        });

        if (jugadores.isEmpty()) {
            throw new NoHayJugadoresEnLaSalaDeEsperaException();
        }

        return jugadores;
    }

    @Override
    public List<Long> verificarSiHayJugadoresQueNoEstenListos(Map<Long, Boolean> jugadores) {
        List<Long> jugadoresNoListos = new ArrayList<>();

        for (Map.Entry<Long, Boolean> entry : jugadores.entrySet()) {
            Long jugadorId = entry.getKey();
            Boolean listo = entry.getValue();

            if (!listo) {
                jugadoresNoListos.add(jugadorId);
            }
        }

        return jugadoresNoListos;
    }

    @Override
    public List<Usuario> crearUsuariosParaQueNoSeRompaLaVistaJuego() {
        List<Usuario> usuarios = new ArrayList<>();

        Usuario usuario1 = new Usuario();
        Usuario usuario2 = new Usuario();

        usuario1.setNombre("Pepitoxx");
        usuario2.setNombre("Alfonsoww");

        usuario1.setId(1L);
        usuario2.setId(2L);

        //agrego los usuarios a la lista
        usuarios.add(usuario1);
        usuarios.add(usuario2);

        return usuarios;
    }
}

