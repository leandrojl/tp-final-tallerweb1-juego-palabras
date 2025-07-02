package com.tallerwebi.helpers;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;
import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class HelperPalabra {


    public Map<Palabra, List<Definicion>> getPalabraYDescripcion(String idioma) {
        Map<Palabra, List<Definicion>> palabraYDescripcion = new HashMap<>();

        String textoPalabra;
        List<String> descripciones;

        if (idioma.equalsIgnoreCase("Español")) {
            HelperRAEApi raeHelper = new HelperRAEApi();
            textoPalabra = raeHelper.obtenerPalabraAleatoria();
            descripciones = raeHelper.obtenerDefiniciones(textoPalabra);
        } else {
            textoPalabra = obtenerPalabraDesdeQidAleatorio(idioma);
            HelperDefinicion helper = new HelperDefinicion();
            descripciones = helper.obtenerDescripcionDesdeWikidata(textoPalabra, idioma);
        }

        if (descripciones == null || descripciones.isEmpty()) {
            descripciones = List.of("No se pudo obtener una definición.");
        }

        Palabra palabra = new Palabra();
        palabra.setDescripcion(textoPalabra);
        palabra.setIdioma(idioma);

        List<Definicion> definiciones = new ArrayList<>();
        for (String descripcion : descripciones) {
            Definicion def = new Definicion("Definición");
            def.setDefinicion(descripcion);
            def.setPalabra(palabra);
            definiciones.add(def);
        }

        palabra.setDefiniciones(definiciones);
        palabraYDescripcion.put(palabra, definiciones);

        return palabraYDescripcion;
    }

    public String obtenerPalabraDesdeQidAleatorio(String idioma) {
        String idiomaCodigo = getCodigoIdioma(idioma);
        int intentosMaximos = 50; // Más intentos
        Random random = new Random();

        for (int intento = 0; intento < intentosMaximos; intento++) {
            int numero = 10_000 + random.nextInt(490_000); // rango más pequeño
            String qid = "Q" + numero;
            String url = "https://www.wikidata.org/wiki/Special:EntityData/" + qid + ".json";

            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) continue;

                JSONObject json = new JSONObject(response.body());
                JSONObject entidad = json.getJSONObject("entities").getJSONObject(qid);

                if (entidad.has("labels") && entidad.getJSONObject("labels").has(idiomaCodigo)) {
                    String palabra = entidad.getJSONObject("labels").getJSONObject(idiomaCodigo).getString("value");
                    if (esPalabraValida(palabra) && palabra.length() > 2) {
                        return palabra;
                    }
                }

            } catch (Exception e) {
                // opcional: log.error("Error al obtener QID: " + qid, e);
                continue;
            }
        }

        return "palabra"; // fallback
    }

    private boolean esPalabraValida(String palabra) {
        return palabra.matches("^[\\p{L} '\\-]+$"); // Letras unicode, espacio, apóstrofe y guión
    }

    private String getCodigoIdioma(String idioma) {
        switch (idioma) {
            case "Español":
                return "es";
            default:
                return "en";
        }
    }

    public List<String> getDefinicion(String palabra, String idioma) {
        com.tallerwebi.helpers.HelperDefinicion hd = new com.tallerwebi.helpers.HelperDefinicion();
        return hd.obtenerDescripcionDesdeWikidata(palabra, idioma);
    }

}




/*
    public String getPalabra(String idioma) {
        Random random = new Random();
        try{
            String rutaArchivoPalabras;
            switch (idioma){
                case "Castellano": rutaArchivoPalabras = rutaArchivoPalabrasEnCastellano; break;

                default: rutaArchivoPalabras = rutaArchivoPalabrasEnIngles;
            }
            List<String> listaDePalabras = Files.readAllLines(Paths.get(rutaArchivoPalabras), StandardCharsets.ISO_8859_1);
            int indice = random.nextInt(listaDePalabras.size());
            return listaDePalabras.get(indice);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

 */
