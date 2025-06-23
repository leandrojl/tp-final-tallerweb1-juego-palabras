package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.helpers.HelperPalabra;
import com.tallerwebi.infraestructura.PalabraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.tallerwebi.dominio.PalabraServicio;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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


