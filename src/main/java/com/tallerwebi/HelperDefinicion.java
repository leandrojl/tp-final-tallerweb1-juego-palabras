package com.tallerwebi;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelperDefinicion {

    public static String obtenerDescripcionDesdeWikidata(String palabra, String idioma) {
        try {
            // Codificamos la palabra
            String palabraCodificada = URLEncoder.encode(palabra, "UTF-8");


            // Construimos la URL correctamente
            String url = obtenerURL(palabraCodificada, idioma);

            // Creamos el cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Creamos la solicitud
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // Enviamos la solicitud y obtenemos la respuesta como string
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parseamos la respuesta JSON
            JSONObject json = new JSONObject(response.body());
            JSONArray resultados = json.getJSONArray("search");

            if (resultados.length() > 0) {
                for (int i = 0; i < resultados.length(); i++) {
                    JSONObject resultado = resultados.getJSONObject(i);
                    if (resultado.has("description") && getCodigoIdioma(idioma).equals(resultado.optString("lang", getCodigoIdioma(idioma)))) {
                        return resultado.getString("description");
                    }
                }
                return null;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al consultar Wikidata: " + e.getMessage();
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