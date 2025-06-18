package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.MensajeEnviado;
import com.tallerwebi.dominio.model.Partida;
import com.tallerwebi.dominio.model.Partida2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class Partida2ServiceImpl implements Partida2Service {

    private Partida2Repository partida2Repository;

    @Autowired
    public Partida2ServiceImpl(Partida2Repository partida2Repository) {
        this.partida2Repository = partida2Repository;
    }

    @Override
    public void crearPartida(Partida2 nuevaPartida) {
        partida2Repository.crearPartida(nuevaPartida);
    }


}
