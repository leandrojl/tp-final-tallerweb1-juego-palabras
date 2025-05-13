package com.tallerwebi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class HelperPalabra {

private static final String  rutaArchivoPalabrasEnCastellano = "src/main/resources/palabrasEnCastellano.txt";
private static final String rutaArchivoPalabrasEnIngles="src/main/resources/palabrasEnIngles.txt";

public Map<String, String> getPalabraYDescripcion(String idioma) {
            HashMap<String, String> palabraYDescripcion = new HashMap<>();
            String palabra = getPalabra(idioma);
            String descripcion = getDefinicion(palabra, idioma);
            palabraYDescripcion.put(palabra, descripcion);
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



    public String getDefinicion(String palabra, String idioma) {
            HelperDefinicion hd = new HelperDefinicion();
            return  hd.obtenerDescripcionDesdeWikidata(palabra, idioma);
    }
}
