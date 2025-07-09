package com.tallerwebi.dominio.interfaceService;

public interface AciertoService {

    boolean todosAcertaron(Long partidaId, Long idRonda);

    boolean jugadorYaAcerto(Long idUsuario, Long ronda);

    int registrarAcierto(Long idUsuario, Long ronda);

    int cantidadDeAciertosEnLaRonda(Long rondaId);
}
