package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.model.Acierto;

public interface AciertoRepository {
    boolean jugadorYaAcerto(Long usuarioId, Long rondaId);

    void registrarAcierto(Acierto acierto);

    int contarAciertosPorRondaId(Long rondaId);
}
