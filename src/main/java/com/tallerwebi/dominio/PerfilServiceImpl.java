package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class PerfilServiceImpl implements PerfilService {

    @Override
    public Map<String, Object> obtenerDatosDePerfil() {
        Map<String, Object> modelo = new HashMap<>();
        modelo.put("nombre", "Juan Perez");
        modelo.put("usuario", "Juancito123");
        modelo.put("edad", "15");
        modelo.put("winrate", "70%");
        modelo.put("fotoPerfil","fotoperfil1.png");
        return modelo;
    }
}
