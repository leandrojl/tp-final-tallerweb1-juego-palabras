package com.tallerwebi.dominio.ServicioImplementacion;

import com.tallerwebi.dominio.DTO.DtoIntento;
import com.tallerwebi.dominio.interfaceService.GeminiBotService;
import com.tallerwebi.dominio.interfaceService.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GeminiBotServiceImpl implements GeminiBotService {

    private final PartidaService partidaService;
    private final RestTemplate restTemplate = new RestTemplate();

    // API KEY real
    private final String apiKey = "AIzaSyAmplCVPAM6GwOiM_18UH0INtvD9s3Y_Y8";
    private final String model = "gemini-2.5-flash-lite-preview-06-17";
    private final String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

    @Autowired
    public GeminiBotServiceImpl(@Lazy PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @Async
    @Override
    public void generateAndSubmitGuess(String definition, Long idPartida) {
        try {
            // 1. Simular retraso
            long delay = 3000 + new Random().nextInt(5000);
            Thread.sleep(delay);

            // 2. Crear cuerpo de la petición
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", "Basado en la siguiente definición, decime la palabra exacta que se está describiendo. " +
                    "Respondé solo con esa palabra, sin explicaciones ni signos de puntuación. " +
                    "Definición: \"" + definition + "\"");

            Map<String, Object> content = new HashMap<>();
            content.put("role", "user");
            content.put("parts", List.of(part));

            requestBody.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 3. Enviar solicitud POST
            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

            // 4. Procesar respuesta
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<String, Object> contentResult = (Map<String, Object>) candidates.get(0).get("content");
                List<Map<String, Object>> parts = (List<Map<String, Object>>) contentResult.get("parts");

                if (parts != null && !parts.isEmpty()) {
                    String guessedWord = parts.get(0).get("text").toString().trim();

                    // 5. Enviar intento a la partida
                    int tiempoRestante = (int) ((60000 - delay) / 1000);
                    DtoIntento intento = new DtoIntento(guessedWord, 4L, idPartida, tiempoRestante);
                    partidaService.procesarIntento(intento, "bot");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al generar intento del bot: " + e.getMessage());
            e.printStackTrace();
        }
    }
}