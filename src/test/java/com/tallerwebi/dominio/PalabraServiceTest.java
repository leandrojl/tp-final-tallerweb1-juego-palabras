package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.helpers.HelperPalabra;
import com.tallerwebi.dominio.interfaceRepository.PalabraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.tallerwebi.dominio.PalabraServicio;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PalabraServiceTest {

    @Spy
    private HelperPalabra helperPalabraSpy = new HelperPalabra();

    @Mock
    private PalabraRepository palabraRepositorio;

    @InjectMocks
    private PalabraServicioImpl palabraService;

    @Test
    public void obtenerPalabraConDefinicionesDesdeHelper_noDevuelveNull() {
        // Solo probamos que el método no devuelva null al pedir la palabra en castellano
        Palabra resultado = palabraService.obtenerPalabraConDefinicionesDesdeHelper("Castellano");

        assertNotNull(resultado, "La palabra no debería ser null");
        assertNotNull(resultado.getDescripcion(), "La descripción de la palabra no debería ser null");
    }




}


