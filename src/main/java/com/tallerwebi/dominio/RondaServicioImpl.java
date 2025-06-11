package com.tallerwebi.dominio;

import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional

public class RondaServicioImpl implements RondaService {

    private final RondaRepository rondaRepository;
    private final PalabraService palabraService;

    public RondaServicioImpl(RondaRepository rondaRepository, PalabraService palabraService) {
        this.rondaRepository = rondaRepository;
        this.palabraService = palabraService;
    }

    @Override
    public Ronda crearNuevaRonda(Partida2 partida, String idioma) {
        HashMap<Palabra, List<Definicion>> palabraYDef = palabraService.traerPalabraYDefinicion(idioma);
        if (palabraYDef == null || palabraYDef.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron palabras para crear la ronda");
        }

        Map.Entry<Palabra, List<Definicion>> entrada = palabraYDef.entrySet().iterator().next();
        Palabra palabra = entrada.getKey();
        palabra.setDefinicion(entrada.getValue());
        palabra.setIdioma(idioma.equalsIgnoreCase("Castellano") ? "es" : "en");

        Ronda ronda = new Ronda();
        ronda.setPartida(partida);
        ronda.setPalabra(palabra);
        ronda.setEstado(Estado.EN_CURSO);
        ronda.setFechaHora(LocalDateTime.now());

        // Asignar el número de ronda correcto
        Ronda ultimaRonda = rondaRepository.buscarUltimaRondaDePartida(partida);
        int nuevoNumero = (ultimaRonda == null) ? 1 : ultimaRonda.getNumeroDeRonda() + 1;
        ronda.setNumeroDeRonda(nuevoNumero);

        // Asignar la definición (ejemplo: la primera definición)
        if (!entrada.getValue().isEmpty()) {
            ronda.setDefinicion(entrada.getValue().get(0).getDefinicion());
        }

        return rondaRepository.guardar(ronda);
    }


    private Palabra extraerPalabra(HashMap<Palabra, List<Definicion>> palabraYDef, String idioma) {
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
}