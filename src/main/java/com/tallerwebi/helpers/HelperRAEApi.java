package com.tallerwebi.helpers;

import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HelperRAEApi {

    private static final String RANDOM_URL = "https://rae-api.com/api/random";

    public String obtenerPalabraAleatoria() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RANDOM_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());
            JSONObject data = json.getJSONObject("data");

            return data.getString("word");

        } catch (Exception e) {
            e.printStackTrace();
            return "palabra"; // fallback
        }
    }

    public List<String> obtenerDefiniciones(String palabra) {
        List<String> definiciones = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://rae-api.com/api/words/" + palabra))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONObject json = new JSONObject(response.body());

            if (!json.getBoolean("ok")) {
                definiciones.add("No se encontró definición para la palabra: " + palabra);
                return definiciones;
            }

            JSONArray meanings = json.getJSONObject("data").getJSONArray("meanings");

            for (int i = 0; i < meanings.length(); i++) {
                JSONObject meaning = meanings.getJSONObject(i);
                JSONArray senses = meaning.getJSONArray("senses");
                for (int j = 0; j < senses.length(); j++) {
                    JSONObject sense = senses.getJSONObject(j);
                    String descripcion = sense.optString("description", "");
                    if (!descripcion.isEmpty()) {
                        definiciones.add(descripcion);
                    }
                }
            }

        } catch (Exception e) {
            definiciones.add("Error al obtener definiciones desde RAE: " + e.getMessage());
        }

        return definiciones;
    }


}


