package com.tallerwebi.helpers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Component
public class HelperPalabra implements IPalabraHelper {

    private final HttpClient httpClient;
    private final Random random;

    public HelperPalabra() {
        this.httpClient = HttpClient.newHttpClient();
        this.random = new Random();
    }

    @Override
    public Map<String, List<String>> getPalabraYDescripcion(String idioma) {
        Map<String, List<String>> palabraYDescripcion = new HashMap<>();

        // Obtener múltiples palabras para mayor variedad
        List<String> palabras = obtenerPalabrasAleatorias(idioma, 5);

        for (String palabra : palabras) {
            List<String> definiciones = getDefinicion(palabra, idioma);
            if (definiciones != null && !definiciones.isEmpty()) {
                palabraYDescripcion.put(palabra, definiciones);
            }
        }

        return palabraYDescripcion;
    }

    @Override
    public String getPalabra(String idioma) {
        List<String> palabras = obtenerPalabrasAleatorias(idioma, 1);
        return palabras.isEmpty() ? null : palabras.get(0);
    }

    @Override
    public List<String> getDefinicion(String palabra, String idioma) {
        return HelperDefinicion.obtenerDescripcionDesdeWikidata(palabra, idioma);
    }

    /**
     * Obtiene palabras aleatorias directamente desde Wikidata
     * Usa diferentes estrategias de búsqueda para obtener variedad
     */
    private List<String> obtenerPalabrasAleatorias(String idioma, int cantidad) {
        List<String> palabras = new ArrayList<>();

        // Estrategia 1: Búsqueda por letra aleatoria
        palabras.addAll(buscarPorLetraAleatoria(idioma, cantidad));

        // Estrategia 2: Búsqueda por entidades populares
        if (palabras.size() < cantidad) {
            palabras.addAll(buscarEntidadesPopulares(idioma, cantidad - palabras.size()));
        }

        // Estrategia 3: Búsqueda por consulta SPARQL simplificada
        if (palabras.size() < cantidad) {
            palabras.addAll(buscarConSPARQL(idioma, cantidad - palabras.size()));
        }

        return palabras.subList(0, Math.min(palabras.size(), cantidad));
    }

    /**
     * Busca palabras que empiecen con una letra aleatoria
     */
    private List<String> buscarPorLetraAleatoria(String idioma, int cantidad) {
        List<String> palabras = new ArrayList<>();
        String codigoIdioma = getCodigoIdioma(idioma);

        // Generar letra aleatoria
        char letraAleatoria = (char) ('a' + random.nextInt(26));

        try {
            String url = "https://www.wikidata.org/w/api.php?action=wbsearchentities"
                    + "&search=" + letraAleatoria
                    + "&language=" + codigoIdioma
                    + "&format=json"
                    + "&limit=50"
                    + "&uselang=" + codigoIdioma;

            palabras.addAll(ejecutarBusqueda(url, cantidad));

        } catch (Exception e) {
            System.err.println("Error en búsqueda por letra: " + e.getMessage());
        }

        return palabras;
    }

    /**
     * Busca entidades populares usando términos generales
     */
    private List<String> buscarEntidadesPopulares(String idioma, int cantidad) {
        List<String> palabras = new ArrayList<>();
        String codigoIdioma = getCodigoIdioma(idioma);

        // Términos muy generales que suelen dar muchos resultados
        String[] terminosGenerales = {"", "a", "e", "o", "i", "u"};
        String termino = terminosGenerales[random.nextInt(terminosGenerales.length)];

        try {
            String url = "https://www.wikidata.org/w/api.php?action=wbsearchentities"
                    + "&search=" + termino
                    + "&language=" + codigoIdioma
                    + "&format=json"
                    + "&limit=100"
                    + "&uselang=" + codigoIdioma;

            palabras.addAll(ejecutarBusqueda(url, cantidad));

        } catch (Exception e) {
            System.err.println("Error en búsqueda de entidades populares: " + e.getMessage());
        }

        return palabras;
    }

    /**
     * Usa consulta SPARQL para obtener entidades aleatorias
     */
    private List<String> buscarConSPARQL(String idioma, int cantidad) {
        List<String> palabras = new ArrayList<>();
        String codigoIdioma = getCodigoIdioma(idioma);

        try {
            // Consulta SPARQL para obtener entidades aleatorias con etiquetas
            String sparqlQuery = URLEncoder.encode(
                    "SELECT ?item ?itemLabel WHERE { " +
                            "  ?item wdt:P31 ?type . " +
                            "  ?item rdfs:label ?itemLabel . " +
                            "  FILTER(LANG(?itemLabel) = \"" + codigoIdioma + "\") " +
                            "  FILTER(STRLEN(?itemLabel) > 2) " +
                            "  FILTER(STRLEN(?itemLabel) < 20) " +
                            "} LIMIT 100", "UTF-8");

            String url = "https://query.wikidata.org/sparql?query=" + sparqlQuery + "&format=json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("User-Agent", "HelperPalabra/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONObject results = json.getJSONObject("results");
                JSONArray bindings = results.getJSONArray("bindings");

                List<String> todasLasPalabras = new ArrayList<>();
                for (int i = 0; i < bindings.length(); i++) {
                    JSONObject binding = bindings.getJSONObject(i);
                    if (binding.has("itemLabel")) {
                        String palabra = binding.getJSONObject("itemLabel").getString("value");
                        if (esPalabraValida(palabra)) {
                            todasLasPalabras.add(palabra);
                        }
                    }
                }

                // Seleccionar palabras aleatorias
                Collections.shuffle(todasLasPalabras);
                palabras.addAll(todasLasPalabras.subList(0, Math.min(todasLasPalabras.size(), cantidad)));
            }

        } catch (Exception e) {
            System.err.println("Error en consulta SPARQL: " + e.getMessage());
        }

        return palabras;
    }

    /**
     * Ejecuta una búsqueda HTTP y extrae las palabras
     */
    private List<String> ejecutarBusqueda(String url, int cantidad) {
        List<String> palabras = new ArrayList<>();

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject json = new JSONObject(response.body());
                JSONArray resultados = json.getJSONArray("search");

                List<String> todasLasPalabras = new ArrayList<>();
                for (int i = 0; i < resultados.length(); i++) {
                    JSONObject resultado = resultados.getJSONObject(i);
                    if (resultado.has("label")) {
                        String palabra = resultado.getString("label");
                        if (esPalabraValida(palabra)) {
                            todasLasPalabras.add(palabra);
                        }
                    }
                }

                // Mezclar y seleccionar aleatoriamente
                Collections.shuffle(todasLasPalabras);
                palabras.addAll(todasLasPalabras.subList(0, Math.min(todasLasPalabras.size(), cantidad)));
            }

        } catch (Exception e) {
            System.err.println("Error ejecutando búsqueda: " + e.getMessage());
        }

        return palabras;
    }

    /**
     * Obtiene el código de idioma
     */
    private String getCodigoIdioma(String idioma) {
        switch (idioma) {
            case "Castellano":
                return "es";
            default:
                return "en";
        }
    }

    /**
     * Valida si una palabra es apropiada para el juego
     */
    private boolean esPalabraValida(String palabra) {
        return palabra != null
                && palabra.length() >= 3
                && palabra.length() <= 15
                && palabra.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$") // Solo letras y espacios
                && !palabra.trim().isEmpty()
                && !palabra.contains("(") // Evitar entradas con paréntesis
                && !palabra.contains(")");
    }

    /**
     * Método público para obtener múltiples palabras con definiciones
     */
    public Map<String, List<String>> obtenerMultiplesPalabrasConDefiniciones(String idioma, int cantidad) {
        Map<String, List<String>> resultado = new HashMap<>();
        List<String> palabras = obtenerPalabrasAleatorias(idioma, cantidad);

        for (String palabra : palabras) {
            List<String> definiciones = getDefinicion(palabra, idioma);
            if (definiciones != null && !definiciones.isEmpty()) {
                resultado.put(palabra, definiciones);
            }
        }

        return resultado;
    }
}