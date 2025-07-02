package com.tallerwebi.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import java.net.URLEncoder;
import java.net.http.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.json.JSONArray;
import org.json.JSONObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HelperDefinicion {

    public static List<String> obtenerDescripcionDesdeWikidata(String palabra, String idioma) {
        try {

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
            e.printStackTrace();
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
            case "Español": return "es";
            default: return "en";
        }
    }
}