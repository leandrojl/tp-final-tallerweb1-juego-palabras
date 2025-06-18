package com.tallerwebi.dominio;

import com.tallerwebi.dominio.interfaceService.PartidaService;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.*;

public class PartidaServiceTest {

    private PartidaService partidaServiceMock;

    @BeforeEach
    void setUp() {
        partidaServiceMock = mock(PartidaService.class);
    }



}
