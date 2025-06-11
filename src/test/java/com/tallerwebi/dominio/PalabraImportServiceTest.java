package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.helpers.IPalabraHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PalabraImportServiceTest {

    @Mock
    private PalabraRepository palabraRepository;

    @Mock
    private IPalabraHelper palabraHelper;

    private PalabraImportService palabraImportService;

    @BeforeEach
    void setUp() {
        palabraImportService = new PalabraImportService(palabraRepository, palabraHelper);
    }

    @Test
    void deberiaImportarPalabrasEnCastellanoCorrectamente() {
        String idioma = "Castellano";
        Map<String, List<String>> palabrasYDescripciones = new HashMap<>();
        palabrasYDescripciones.put("casa", Arrays.asList("Edificio destinado a vivienda", "Hogar familiar"));
        palabrasYDescripciones.put("perro", Arrays.asList("Animal doméstico canino", "Mascota fiel"));

        when(palabraHelper.getPalabraYDescripcion(idioma)).thenReturn(palabrasYDescripciones);

        palabraImportService.importarPalabraDesdeAPI(idioma);

        verify(palabraRepository, times(2)).guardar(any(Palabra.class));

        verify(palabraRepository).guardar(argThat(palabra ->
                palabra.getDescripcion().equals("casa") &&
                        palabra.getIdioma().equals("es") &&
                        palabra.getDefinicion().size() == 2
        ));

        verify(palabraRepository).guardar(argThat(palabra ->
                palabra.getDescripcion().equals("perro") &&
                        palabra.getIdioma().equals("es") &&
                        palabra.getDefinicion().size() == 2
        ));
    }

    @Test
    void deberiaImportarPalabrasEnInglesCorrectamente() {
        String idioma = "English";
        Map<String, List<String>> palabrasYDescripciones = new HashMap<>();
        palabrasYDescripciones.put("house", Arrays.asList("A building for human habitation", "Place where people live"));
        palabrasYDescripciones.put("dog", Arrays.asList("Domestic carnivorous mammal", "Man's best friend"));

        when(palabraHelper.getPalabraYDescripcion(idioma)).thenReturn(palabrasYDescripciones);

        palabraImportService.importarPalabraDesdeAPI(idioma);

        verify(palabraRepository, times(2)).guardar(any(Palabra.class));

        verify(palabraRepository).guardar(argThat(palabra ->
                palabra.getDescripcion().equals("house") &&
                        palabra.getIdioma().equals("en") &&
                        palabra.getDefinicion().size() == 2
        ));

        verify(palabraRepository).guardar(argThat(palabra ->
                palabra.getDescripcion().equals("dog") &&
                        palabra.getIdioma().equals("en") &&
                        palabra.getDefinicion().size() == 2
        ));
    }

    @Test
    void deberiaManejarmapVacioCorrectamente() {
        String idioma = "Castellano";
        Map<String, List<String>> palabrasVacias = new HashMap<>();

        when(palabraHelper.getPalabraYDescripcion(idioma)).thenReturn(palabrasVacias);

        palabraImportService.importarPalabraDesdeAPI(idioma);

        verify(palabraRepository, never()).guardar(any(Palabra.class));
    }

    @Test
    void deberiaManejarPalabraConDefinicionesVacias() {
        String idioma = "Castellano";
        Map<String, List<String>> palabrasConListaVacia = new HashMap<>();
        palabrasConListaVacia.put("palabra", Collections.emptyList());

        when(palabraHelper.getPalabraYDescripcion(idioma)).thenReturn(palabrasConListaVacia);

        palabraImportService.importarPalabraDesdeAPI(idioma);

        verify(palabraRepository, times(1)).guardar(argThat(palabra ->
                palabra.getDescripcion().equals("palabra") &&
                        palabra.getIdioma().equals("es") &&
                        palabra.getDefinicion().isEmpty()
        ));
    }
}
