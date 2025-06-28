package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceRepository.Partida2Repository;
import com.tallerwebi.dominio.interfaceService.Partida2Service;
import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

@Service
@Transactional
public class Partida2ServiceImpl implements Partida2Service {

    private Partida2Repository partida2Repository;

    @Autowired
    public Partida2ServiceImpl(Partida2Repository partida2Repository) {
        this.partida2Repository = partida2Repository;
    }

    @Override
    public DefinicionDto iniciarPrimerRonda(Long partidaId) {
        return null;
    }

    @Override
    public Serializable crearPartida(Partida2 nuevaPartida) {
        return partida2Repository.crearPartida(nuevaPartida);
    }

    /*@Override
            public DefinicionDto iniciarPrimerRonda(Long partidaId) {
            // Obtener la partida (usando el repositorio, o el método que tengas para acceder a la partida)
            Partida2 partida = partida2Repository.buscarPorId(partidaId);

            if (partida == null) {
                throw new IllegalArgumentException("No existe la partida con ID: " + partidaId);
            }

            String idioma = partida.getIdioma();

            // Crear la ronda usando el idioma de la partida
            Ronda ronda = rondaService.crearRonda(partidaId, idioma);

            Palabra palabra = ronda.getPalabra();

            // Obtenemos una definición (por ejemplo la primera)
            String definicionTexto = palabra.getDefiniciones().stream()
                    .findFirst()
                    .map(Definicion::getDefinicion)
                    .orElse("Definición no disponible");

            // Armar el DTO para enviar al frontend
            DefinicionDto dto = new DefinicionDto();
            dto.setPalabra(palabra.getDescripcion());
            dto.setDefinicion(definicionTexto);
            dto.setNumeroRonda(ronda.getNumeroDeRonda());

            return dto;

    }*/

    /*@Override
    public Serializable crearPartida(Partida2 nuevaPartida) {
        return partida2Repository.crearPartida(nuevaPartida);

    }*/


}
