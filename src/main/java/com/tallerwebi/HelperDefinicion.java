package com.tallerwebi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HelperDefinicion {

    public static List<String> obtenerDescripcionDesdeWikidata(String palabra, String idioma) {
        try {

            String palabraCodificada = URLEncoder.encode(palabra, "UTF-8");
            String url = obtenerURL(palabraCodificada, idioma);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            JSONArray resultados = json.getJSONArray("search");

            if (resultados.length() > 0) {
                List<String> descripciones = new ArrayList<>();
                for (int i = 0; i < resultados.length(); i++) {
                    JSONObject resultado = resultados.getJSONObject(i);
                    if (resultado.has("description") && getCodigoIdioma(idioma).equals(resultado.optString("lang", getCodigoIdioma(idioma)))) {
                        descripciones.add(resultado.getString("description"));
                    }
                }
                return descripciones;
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    private static String obtenerURL(String palabraCodificada, String idioma) {
        String codigoIdioma = getCodigoIdioma(idioma);
     return   "https://www.wikidata.org/w/api.php?action=wbsearchentities"
                + "&search=" + palabraCodificada
                + "&language="+codigoIdioma
                + "&format=json"
                + "&uselang="+codigoIdioma;
    }

    private static String getCodigoIdioma(String idioma) {
        switch(idioma){
            case "Castellano": return "es";
            default: return "en";
        }
    }
}