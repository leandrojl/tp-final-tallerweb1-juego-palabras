package com.tallerwebi.dominio;


import com.tallerwebi.dominio.Enum.Estado;
import com.tallerwebi.dominio.interfaceService.PalabraServicio;
import com.tallerwebi.dominio.interfaceService.RondaService;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.helpers.HelperPalabra;
import com.tallerwebi.dominio.interfaceRepository.PartidaRepository;
import com.tallerwebi.infraestructura.RondaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@Transactional
public class RondaServiceImpl implements RondaService {

    private final int MAX_RONDAS = 5;
    private int rondaActual = 1;
    private final HelperPalabra helperPalabra = new HelperPalabra();



    @Autowired
    private RondaRepository rondaRepositorio;

    @Autowired
    private PartidaRepository partidaRepositorio;

    @Autowired
    private PalabraServicio palabraServicio;

    @Override
    public Ronda crearRonda(Long partidaId, String idioma) {
        Partida partida = partidaRepositorio.buscarPorId(partidaId);
        if (partida == null) {
            throw new IllegalArgumentException("No se encontró la partida con ID: " + partidaId);
        }

        int numeroDeRonda = rondaRepositorio.obtenerCantidadDeRondasPorPartida(partidaId) + 1;

        // ✅ Usamos palabra que ya tiene sus definiciones asociadas
        Palabra palabra = palabraServicio.obtenerPalabraConDefinicionesDesdeHelper(idioma);

        Ronda nuevaRonda = new Ronda();
        nuevaRonda.setPartida(partida);
        nuevaRonda.setPalabra(palabra);
        nuevaRonda.setNumeroDeRonda(numeroDeRonda);
        nuevaRonda.setEstado(Estado.EN_CURSO);
        nuevaRonda.setFechaHora(LocalDateTime.now());

        rondaRepositorio.guardar(nuevaRonda);

        return nuevaRonda;
    }



}

