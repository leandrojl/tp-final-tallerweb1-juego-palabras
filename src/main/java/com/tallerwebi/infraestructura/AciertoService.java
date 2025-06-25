package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Ronda;

public interface AciertoService {
    boolean jugadorYaAcerto(Long idUsuario, Long ronda);

    int registrarAcierto(Long idUsuario, Long ronda);
}
