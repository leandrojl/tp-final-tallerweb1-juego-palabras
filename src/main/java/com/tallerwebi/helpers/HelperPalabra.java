package com.tallerwebi.helpers;

import com.tallerwebi.dominio.model.Definicion;
import com.tallerwebi.dominio.model.Palabra;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HelperPalabra {

    private static final String  rutaArchivoPalabrasEnCastellano = "src/main/resources/palabrasEnCastellano.txt";
    private static final String rutaArchivoPalabrasEnIngles="src/main/resources/palabrasEnIngles.txt";

    public Map<Palabra, List<Definicion>> getPalabraYDescripcion(String idioma) {
        Map<Palabra, List<Definicion>> palabraYDescripcion = new HashMap<>();

        String textoPalabra = getPalabra(idioma); // sigue trayéndola del .txt

        List<String> descripciones = getDefinicion(textoPalabra, idioma); // devuelve List<String>

        // Crear objeto Palabra
        Palabra palabra = new Palabra();
        palabra.setDescripcion(textoPalabra);
        palabra.setIdioma(idioma);

        // Convertir descripciones a entidades Definicion
        List<Definicion> definiciones = new ArrayList<>();
        for (String descripcion : descripciones) {
            Definicion def = new Definicion("Lugar donde se vive");
            def.setDefinicion(descripcion);
            def.setPalabra(palabra);
            definiciones.add(def);
        }

        // Relacionar y armar map
        palabra.setDefiniciones(definiciones);
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
        return  hd.obtenerDescripcionDesdeWikidata(palabra, idioma);
    }
}