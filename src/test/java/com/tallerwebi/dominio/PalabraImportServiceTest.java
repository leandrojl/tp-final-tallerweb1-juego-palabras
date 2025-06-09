package com.tallerwebi.dominio;

import com.tallerwebi.helpers.HelperPalabra;
import com.tallerwebi.dominio.model.Palabra;
import com.tallerwebi.dominio.model.Definicion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PalabraImportServiceTest {

    private PalabraRepositoryImpl palabraRepository;
    private PalabraImportService palabraImportService;

    @BeforeEach
    void setUp() {
        palabraRepository = mock(PalabraRepositoryImpl.class);
        palabraImportService = new PalabraImportService(palabraRepository);
    }

    @Test
    void deberiaImportarPalabrasEnCastellanoCorrectamente() {
        // Given
        String idioma = "Castellano";
        Map<String, List<String>> palabrasYDescripciones = new HashMap<>();
        palabrasYDescripciones.put("casa", Arrays.asList("Edificio destinado a vivienda", "Hogar familiar"));
        palabrasYDescripciones.put("perro", Arrays.asList("Animal doméstico canino", "Mascota fiel"));

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idioma)).thenReturn(palabrasYDescripciones);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idioma);

            // Then
            verify(palabraRepository, times(2)).guardar(any(Palabra.class));

            // Verificar que se guardaron palabras con idioma "es"
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
    }

    @Test
    void deberiaImportarPalabrasEnInglesCorrectamente() {
        // Given
        String idioma = "English";
        Map<String, List<String>> palabrasYDescripciones = new HashMap<>();
        palabrasYDescripciones.put("house", Arrays.asList("A building for human habitation", "Place where people live"));
        palabrasYDescripciones.put("dog", Arrays.asList("Domestic carnivorous mammal", "Man's best friend"));

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idioma)).thenReturn(palabrasYDescripciones);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idioma);

            // Then
            verify(palabraRepository, times(2)).guardar(any(Palabra.class));

            // Verificar que se guardaron palabras con idioma "en"
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
    }

    @Test
    void deberiaManejarmapVacioCorrectamente() {
        // Given
        String idioma = "Castellano";
        Map<String, List<String>> palabrasVacias = new HashMap<>();

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idioma)).thenReturn(palabrasVacias);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idioma);

            // Then
            verify(palabraRepository, never()).guardar(any(Palabra.class));
        }
    }

    @Test
    void deberiaManejarPalabraConDefinicionesVacias() {
        // Given
        String idioma = "Castellano";
        Map<String, List<String>> palabrasConListaVacia = new HashMap<>();
        palabrasConListaVacia.put("palabra", Arrays.asList());

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idioma)).thenReturn(palabrasConListaVacia);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idioma);

            // Then
            verify(palabraRepository, times(1)).guardar(any(Palabra.class));
            verify(palabraRepository).guardar(argThat(palabra ->
                    palabra.getDescripcion().equals("palabra") &&
                            palabra.getIdioma().equals("es") &&
                            palabra.getDefinicion().isEmpty()
            ));
        }
    }

    @Test
    void deberiaDetectarIdiomaCorrectamenteConCastellanoEnMinusculas() {
        // Given
        String idioma = "castellano";
        Map<String, List<String>> palabras = new HashMap<>();
        palabras.put("test", Arrays.asList("definicion"));

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idioma)).thenReturn(palabras);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idioma);

            // Then
            verify(palabraRepository).guardar(argThat(palabra ->
                    palabra.getIdioma().equals("es")
            ));
        }
    }

    @Test
    void deberiaAsignarIdiomaEnParaCualquierOtroIdioma() {
        // Given
        String idioma = "Frances";
        Map<String, List<String>> palabras = new HashMap<>();
        palabras.put("maison", Arrays.asList("casa en frances"));

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idioma)).thenReturn(palabras);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idioma);

            // Then
            verify(palabraRepository).guardar(argThat(palabra ->
                    palabra.getIdioma().equals("en")
            ));
        }
    }

    @Test
    void deberiaCrearDefinicionesCorrectamente() {
        // Given
        String idioma = "Castellano";
        Map<String, List<String>> palabras = new HashMap<>();
        palabras.put("gato", Arrays.asList("Animal felino", "Mascota independiente", "Cazador de ratones"));

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idioma)).thenReturn(palabras);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idioma);

            // Then
            verify(palabraRepository).guardar(argThat(palabra -> {
                List<Definicion> definiciones = palabra.getDefinicion();
                return definiciones.size() == 3 &&
                        definiciones.stream().anyMatch(def -> def.getDefinicion().equals("Animal felino")) &&
                        definiciones.stream().anyMatch(def -> def.getDefinicion().equals("Mascota independiente")) &&
                        definiciones.stream().anyMatch(def -> def.getDefinicion().equals("Cazador de ratones"));
            }));
        }
    }

    @Test
    void deberiaManejarmultiplesImportacionesEnElMismoTest() {
        // Given
        String idiomaCastellano = "Castellano";
        String idiomaIngles = "English";

        Map<String, List<String>> palabrasCastellano = new HashMap<>();
        palabrasCastellano.put("libro", Arrays.asList("Conjunto de hojas impresas"));

        Map<String, List<String>> palabrasIngles = new HashMap<>();
        palabrasIngles.put("book", Arrays.asList("Set of printed sheets"));

        try (MockedConstruction<HelperPalabra> mockedHelper = mockConstruction(HelperPalabra.class,
                (mock, context) -> {
                    when(mock.getPalabraYDescripcion(idiomaCastellano)).thenReturn(palabrasCastellano);
                    when(mock.getPalabraYDescripcion(idiomaIngles)).thenReturn(palabrasIngles);
                })) {

            // When
            palabraImportService.importarPalabraDesdeAPI(idiomaCastellano);
            palabraImportService.importarPalabraDesdeAPI(idiomaIngles);

            // Then
            verify(palabraRepository, times(2)).guardar(any(Palabra.class));

            verify(palabraRepository).guardar(argThat(palabra ->
                    palabra.getDescripcion().equals("libro") && palabra.getIdioma().equals("es")
            ));

            verify(palabraRepository).guardar(argThat(palabra ->
                    palabra.getDescripcion().equals("book") && palabra.getIdioma().equals("en")
            ));
        }
    }
}