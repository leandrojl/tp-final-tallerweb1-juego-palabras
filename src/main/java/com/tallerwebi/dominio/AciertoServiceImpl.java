package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.*;
import com.tallerwebi.infraestructura.AciertoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AciertoServiceImpl implements AciertoService{
    private final AciertoRepository aciertoRepository;

    @Autowired
    public AciertoServiceImpl(AciertoRepository aciertoRepository) {
        this.aciertoRepository = aciertoRepository;
    }

    @Override
    public void registrarAcierto(Usuario usuario, Ronda ronda) {
        Acierto acierto = new Acierto();
        acierto.setUsuario(usuario);
        acierto.setRonda(ronda);

        aciertoRepository.guardar(acierto);
    }
}
