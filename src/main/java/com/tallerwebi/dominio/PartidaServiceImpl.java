package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@Transactional
public class PartidaServiceImpl implements PartidaService {

    @Autowired
    private PartidaRepository partidaRepositorio;

    @Autowired
    private RondaService rondaServicio;

    @Autowired
    private PuntajeService puntajeServicio;

    // Mapas en memoria para el estado del juego
    private final Map<Long, Map<String, Object>> estadosPartida = new HashMap<>();
    private final Map<Long, List<String>> jugadoresPartida = new HashMap<>();

    @Override
    public Partida2 crearPartida(String nombre, String idioma, boolean permiteComodin,
                                 int rondasTotales, int minimoJugadores, int maximoJugadores) {
        Partida2 partida = new Partida2(nombre, idioma, permiteComodin, rondasTotales,
                maximoJugadores, minimoJugadores, Estado.EN_ESPERA);
        partidaRepositorio.guardar(partida);

        // Inicializar estado en memoria
        Map<String, Object> estado = new HashMap<>();
        estado.put("rondaActual", 0);
        estado.put("palabraActual", null);
        estado.put("definicionActual", null);
        estado.put("tiempoInicio", null);
        estado.put("jugadoresListos", 0);
        estadosPartida.put(partida.getId(), estado);
        jugadoresPartida.put(partida.getId(), new java.util.ArrayList<>());

        return partida;
    }

    @Override
    public boolean unirseAPartida(Long partidaId, String jugadorId, Jugador jugador) {
        Partida2 partida = partidaRepositorio.buscarPorId(partidaId);
        if (partida == null || partida.getEstado() != Estado.EN_ESPERA) {
            return false;
        }

        List<String> jugadores = jugadoresPartida.get(partidaId);
        if (jugadores.size() >= partida.getMaximoJugadores()) {
            return false;
        }

        if (!jugadores.contains(jugadorId)) {
            jugadores.add(jugadorId);
            puntajeServicio.registrarJugador(jugadorId, jugador);
        }

        // Si alcanzamos el mínimo, cambiar estado
        if (jugadores.size() >= partida.getMinimoJugadores()) {
            partida.setEstado(Estado.EN_CURSO);
            partidaRepositorio.guardar(partida);
        }

        return true;
    }

    @Override
    public boolean iniciarPartida(Long partidaId) {
        Partida2 partida = partidaRepositorio.buscarPorId(partidaId);
        if (partida == null || partida.getEstado() != Estado.EN_CURSO) {
            return false;
        }

        // Crear primera ronda
        Ronda primeraRonda = rondaServicio.crearRonda(partidaId, partida.getIdioma());

        // Actualizar estado
        partida.setEstado(Estado.EN_CURSO);
        partidaRepositorio.guardar(partida);

        Map<String, Object> estado = estadosPartida.get(partidaId);
        estado.put("rondaActual", 1);
        estado.put("palabraActual", primeraRonda.getPalabra().getDescripcion());
        estado.put("definicionActual", primeraRonda.getPalabra().getDefiniciones().get(0).getDefinicion());
        estado.put("tiempoInicio", System.currentTimeMillis());
        estado.put("rondaId", primeraRonda.getId());

        return true;
    }

    @Override
    public ResultadoIntento procesarIntento(Long partidaId, String jugadorId, String intento, int tiempoRestante) {
        Partida2 partida = partidaRepositorio.buscarPorId(partidaId);
        Map<String, Object> estado = estadosPartida.get(partidaId);

        String palabraActual = (String) estado.get("palabraActual");
        boolean esCorrect = intento.equalsIgnoreCase(palabraActual);

        ResultadoIntento resultado = new ResultadoIntento();
        resultado.setCorrecto(esCorrect);
        resultado.setJugadorId(jugadorId);
        resultado.setPalabra(palabraActual);

        if (esCorrect) {
            // Calcular puntos
            int puntos = calcularPuntosSegunTiempo(tiempoRestante);
            puntajeServicio.registrarPuntos(jugadorId, puntos);
            resultado.setPuntosObtenidos(puntos);

            // Avanzar ronda si no es la última
            int rondaActual = (Integer) estado.get("rondaActual");
            if (rondaActual < partida.getRondasTotales()) {
                avanzarRonda(partidaId, partida);
                resultado.setNuevaRonda(true);
                resultado.setNuevaPalabra((String) estado.get("palabraActual"));
                resultado.setNuevaDefinicion((String) estado.get("definicionActual"));
            } else {
                finalizarPartida(partidaId, partida);
                resultado.setPartidaTerminada(true);
            }
        }

        return resultado;
    }

    @Override
    public void finalizarRondaPorTiempo(Long partidaId) {
        Partida2 partida = partidaRepositorio.buscarPorId(partidaId);
        Map<String, Object> estado = estadosPartida.get(partidaId);

        int rondaActual = (Integer) estado.get("rondaActual");
        if (rondaActual < partida.getRondasTotales()) {
            avanzarRonda(partidaId, partida);
        } else {
            finalizarPartida(partidaId, partida);
        }
    }

    private void avanzarRonda(Long partidaId, Partida2 partida) {
        Map<String, Object> estado = estadosPartida.get(partidaId);
        int rondaActual = (Integer) estado.get("rondaActual");

        Ronda nuevaRonda = rondaServicio.crearRonda(partidaId, partida.getIdioma());

        estado.put("rondaActual", rondaActual + 1);
        estado.put("palabraActual", nuevaRonda.getPalabra().getDescripcion());
        estado.put("definicionActual", nuevaRonda.getPalabra().getDefiniciones().get(0).getDefinicion());
        estado.put("tiempoInicio", System.currentTimeMillis());
        estado.put("rondaId", nuevaRonda.getId());
    }

    private void finalizarPartida(Long partidaId, Partida2 partida) {
        partida.setEstado(Estado.FINALIZADA);
        partidaRepositorio.guardar(partida);

        Map<String, Object> estado = estadosPartida.get(partidaId);
        estado.put("partidaTerminada", true);
    }

    private int calcularPuntosSegunTiempo(int tiempoRestante) {
        if (tiempoRestante > 45) return 100;
        else if (tiempoRestante > 30) return 75;
        else if (tiempoRestante > 15) return 50;
        else if (tiempoRestante > 0) return 25;
        else return 0;
    }

    @Override
    public Partida2 obtenerPartidaPorJugador(String jugadorId) {
        for (Map.Entry<Long, List<String>> entry : jugadoresPartida.entrySet()) {
            if (entry.getValue().contains(jugadorId)) {
                return partidaRepositorio.buscarPorId(entry.getKey());
            }
        }
        return null;
    }

    @Override
    public EstadoPartida obtenerEstadoPartida(Long partidaId) {
        Partida2 partida = partidaRepositorio.buscarPorId(partidaId);
        Map<String, Object> estado = estadosPartida.get(partidaId);
        List<String> jugadores = jugadoresPartida.get(partidaId);

        EstadoPartida estadoPartida = new EstadoPartida();
        estadoPartida.setPartidaId(partidaId);
        estadoPartida.setEstado(partida.getEstado());
        estadoPartida.setRondaActual((Integer) estado.get("rondaActual"));
        estadoPartida.setRondasTotales(partida.getRondasTotales());
        estadoPartida.setDefinicionActual((String) estado.get("definicionActual"));
        estadoPartida.setPalabraActual((String) estado.get("palabraActual"));
        estadoPartida.setJugadores(jugadores);
        estadoPartida.setPartidaTerminada((Boolean) estado.getOrDefault("partidaTerminada", false));

        return estadoPartida;
    }


/*
    // Implementaciones restantes del PartidaService original...
    @Override
    public Partida iniciarNuevaPartida(String jugadorId, String nombre) {
        // Mantener compatibilidad con el código anterior
        return null;
    }

    @Override
    public Partida obtenerPartida(String jugadorId) {
        // Mantener compatibilidad con el código anterior
        return null;
    }

    @Override
    public void eliminarPartida(String jugadorId) {
        // Implementar limpieza
    }

    @Override
    public void enviarMensajeAUsuarioEspecifico(String nombreUsuario, String mensaje) {
        // Ya implementado en la clase original
    }

 */
}
