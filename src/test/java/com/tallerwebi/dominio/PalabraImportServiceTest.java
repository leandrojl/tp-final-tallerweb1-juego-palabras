
package com.tallerwebi.dominio;

import com.tallerwebi.dominio.model.Palabra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PalabraImportServiceIntegrationTest {

    @Mock
    private PalabraRepository palabraRepository;

    private PalabraImportService palabraImportService;

    @BeforeEach
    void setUp() {
        // Usar la implementación REAL del helper, no mock
        palabraImportService = new PalabraImportService(palabraRepository, new com.tallerwebi.helpers.HelperPalabra());
    }

    @Test
    void deberiaImportarPalabrasRealesEnCastellanoDesdeWikidata() {
        String idioma = "Castellano";

        // Ejecutar la importación real
        palabraImportService.importarPalabraDesdeAPI(idioma);

        // Verificar que se guardaron palabras
        verify(palabraRepository, atLeast(1)).guardar(any(Palabra.class));

        // Capturar las palabras guardadas para validarlas
        verify(palabraRepository, atLeast(1)).guardar(argThat(palabra -> {
            // Validaciones de las palabras reales obtenidas
            assertNotNull(palabra.getDescripcion(), "La palabra no debe ser nula");
            assertFalse(palabra.getDescripcion().trim().isEmpty(), "La palabra no debe estar vacía");
            assertEquals("es", palabra.getIdioma(), "El idioma debe ser español");
            assertTrue(palabra.getDescripcion().length() >= 3, "La palabra debe tener al menos 3 caracteres");
            assertTrue(palabra.getDescripcion().length() <= 15, "La palabra no debe ser muy larga");

            // Si tiene definiciones, validarlas
            if (palabra.getDefinicion() != null && !palabra.getDefinicion().isEmpty()) {
                palabra.getDefinicion().forEach(def -> {
                    assertNotNull(def.getDefinicion(), "La definición no debe ser nula");
                    assertFalse(def.getDefinicion().trim().isEmpty(), "La definición no debe estar vacía");
                });
            }

            System.out.println("Palabra importada: " + palabra.getDescripcion() +
                    " - Definiciones: " + palabra.getDefinicion().size());
            return true;
        }));
    }

    @Test
    void deberiaImportarPalabrasRealesEnInglesDesdeWikidata() {
        String idioma = "English";

        palabraImportService.importarPalabraDesdeAPI(idioma);

        verify(palabraRepository, atLeast(1)).guardar(any(Palabra.class));

        verify(palabraRepository, atLeast(1)).guardar(argThat(palabra -> {
            // Validaciones de las palabras reales obtenidas
            assertNotNull(palabra.getDescripcion(), "La palabra no debe ser nula");
            assertFalse(palabra.getDescripcion().trim().isEmpty(), "La palabra no debe estar vacía");
            assertEquals("en", palabra.getIdioma(), "El idioma debe ser inglés");
            assertTrue(palabra.getDescripcion().length() >= 3, "La palabra debe tener al menos 3 caracteres");

            // Validar que es una palabra en inglés (caracteres latinos básicos)
            assertTrue(palabra.getDescripcion().matches("^[a-zA-Z\\s]+$"),
                    "La palabra en inglés debe contener solo letras y espacios");

            System.out.println("English word imported: " + palabra.getDescripcion() +
                    " - Definitions: " + palabra.getDefinicion().size());
            return true;
        }));
    }

    @Test
    void deberiaManejarErroresDeAPI() {
        // Test para verificar que maneja errores de API sin fallar
        String idiomaInvalido = "IdiomaInexistente";

        // No debería lanzar excepción
        assertDoesNotThrow(() -> {
            palabraImportService.importarPalabraDesdeAPI(idiomaInvalido);
        });


    }
}