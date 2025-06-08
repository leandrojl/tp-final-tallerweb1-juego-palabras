package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Partida;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.util.AssertionErrors.assertEquals;

public class PartidaServiceTest {

    private PartidaService partidaServiceMock;

    @BeforeEach
    void setUp() {
        partidaServiceMock = mock(PartidaService.class);
    }



}
