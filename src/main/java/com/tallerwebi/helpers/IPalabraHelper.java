package com.tallerwebi.helpers;

import java.util.List;
import java.util.Map;

public interface IPalabraHelper {
    Map<String, List<String>> getPalabraYDescripcion(String idioma);

    String getPalabra(String idioma);

    List<String> getDefinicion(String palabra, String idioma);
}
