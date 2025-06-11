package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RondaServicioImpl implements RondaService {

    private final RondaRepository rondaRepository;
    private final PalabraService palabraService; // El servicio actual renombrado

    public RondaServicioImpl(RondaRepository rondaRepository, PalabraService palabraService) {
        this.rondaRepository = rondaRepository;
        this.palabraService = palabraService;
    }

    @Override
    public Ronda crearNuevaRonda(Partida2 partida, String idioma) {
        Long cantidadRondasActuales = rondaRepository.contarRondasDePartida(partida);
        if (cantidadRondasActuales == null) {
            cantidadRondasActuales = 0L;
        }
        if (cantidadRondasActuales >= partida.getRondasTotales()) {
            throw new IllegalStateException("Ya se jugaron todas las rondas permitidas para esta partida.");
        }

        // Usar palabraService para obtener palabra
        HashMap<Palabra, List<Definicion>> palabraYDef = palabraService.traerPalabraYDefinicion(idioma);

        // Crear la ronda completa
        Ronda ronda = new Ronda();
        ronda.setPartida(partida);
        ronda.setPalabra(extraerPalabra(palabraYDef, idioma)); // Pasar el idioma
        ronda.setEstado(Estado.EN_CURSO);
        ronda.setFechaHora(LocalDateTime.now());
        ronda.setNumeroDeRonda(cantidadRondasActuales + 1);

        return rondaRepository.guardar(ronda);
    }

    @Override
    public Ronda buscarRondaActivaPorPartidaId(Long id) {
        return this.rondaRepository.buscarRondaActivaPorPartidaId(id);
    }

    @Override
    public Ronda guardar(Ronda rondaActual) {
        return this.rondaRepository.guardar(rondaActual);
    }

    @Override
    public Palabra extraerPalabra(HashMap<Palabra, List<Definicion>> palabraYDef, String idioma) {
        // Verificar que el HashMap no esté vacío
        if (palabraYDef == null || palabraYDef.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron palabras para crear la ronda");
        }

        // Obtener la primera entrada del HashMap
        Map.Entry<Palabra, List<Definicion>> entrada = palabraYDef.entrySet().iterator().next();

        // Crear y configurar la palabra
        Palabra palabra = entrada.getKey();
        palabra.setDefinicion(entrada.getValue());

        // Asignar el idioma basado en el parámetro
        palabra.setIdioma(idioma.equalsIgnoreCase("Castellano") ? "es" : "en");

        return palabra;
    }

    @Override
    public void desactivarRonda(Ronda rondaActual) {
        rondaActual.setEstado(Estado.FINALIZADA);
        rondaRepository.actualizar(rondaActual);
    }
}