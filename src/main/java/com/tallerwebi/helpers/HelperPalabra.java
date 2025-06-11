package com.tallerwebi.helpers;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
@Component
public class HelperPalabra implements IPalabraHelper {

    private static final String rutaArchivoPalabrasEnCastellano = "src/main/resources/palabrasEnCastellano.txt";
    private static final String rutaArchivoPalabrasEnIngles = "src/main/resources/palabrasEnIngles.txt";

    @Override
    public Map<String, List<String>> getPalabraYDescripcion(String idioma) {
        Map<String, List<String>> palabraYDescripcion = new HashMap<>();
        String palabra = getPalabra(idioma);
        List<String> definiciones = getDefinicion(palabra, idioma);
        palabraYDescripcion.put(palabra, definiciones);
        return palabraYDescripcion;
    }

    @Override
    public String getPalabra(String idioma) {
        Random random = new Random();
        try {
            String rutaArchivoPalabras;
            switch (idioma) {
                case "Castellano":
                    rutaArchivoPalabras = rutaArchivoPalabrasEnCastellano;
                    break;
                default:
                    rutaArchivoPalabras = rutaArchivoPalabrasEnIngles;
            }
            List<String> listaDePalabras = Files.readAllLines(Paths.get(rutaArchivoPalabras), StandardCharsets.ISO_8859_1);
            int indice = random.nextInt(listaDePalabras.size());
            return listaDePalabras.get(indice);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getDefinicion(String palabra, String idioma) {
        com.tallerwebi.helpers.HelperDefinicion hd = new com.tallerwebi.helpers.HelperDefinicion();
        return HelperDefinicion.obtenerDescripcionDesdeWikidata(palabra, idioma);
    }
}