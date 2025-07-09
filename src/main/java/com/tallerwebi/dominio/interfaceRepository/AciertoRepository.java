package com.tallerwebi.dominio.interfaceRepository;

import com.tallerwebi.dominio.model.Acierto;


public interface AciertoRepository {

    long contarUsuariosQueAcertaron(Long idRonda);

    boolean jugadorYaAcerto(Long usuarioId, Long rondaId);

    void registrarAcierto(Acierto acierto);

    int contarAciertosPorRondaId(Long rondaId);

}
