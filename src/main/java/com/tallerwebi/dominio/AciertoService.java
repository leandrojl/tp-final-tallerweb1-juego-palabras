package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Partida2;
import com.tallerwebi.dominio.model.Ronda;
import com.tallerwebi.dominio.model.Usuario;

public interface AciertoService {
    void registrarAcierto(Usuario usuario, Ronda ronda);
}
