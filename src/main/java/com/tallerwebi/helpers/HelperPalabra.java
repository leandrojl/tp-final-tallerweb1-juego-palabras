package com.tallerwebi.helpers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HelperPalabra {

    private static final String  rutaArchivoPalabrasEnCastellano = "src/main/resources/palabrasEnCastellano.txt";
    private static final String rutaArchivoPalabrasEnIngles="src/main/resources/palabrasEnIngles.txt";

    public Map<String, List<String>> getPalabraYDescripcion(String idioma) {
        Map<String, List<String>> palabraYDescripcion = new HashMap<>();
        String palabra = getPalabra(idioma);
        List<String> definiciones = getDefinicion(palabra, idioma);
        palabraYDescripcion.put(palabra, definiciones);
        return palabraYDescripcion;
    }


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


    public List<String> getDefinicion(String palabra, String idioma) {
        com.tallerwebi.helpers.HelperDefinicion hd = new com.tallerwebi.helpers.HelperDefinicion();
        return  HelperDefinicion.obtenerDescripcionDesdeWikidata(palabra, idioma);
    }
}