package com.tallerwebi.dominio;

public class DefinicionDto {


private int numeroDeRonda;
private String definicionTexto;
private String palabra;

    public DefinicionDto(String palabra, String definicionTexto, int numeroDeRonda) {
        this.palabra = palabra;
        this.definicionTexto = definicionTexto;
        this.numeroDeRonda = numeroDeRonda;
    }

    public DefinicionDto(){


    }


    public void setNumeroRonda(int numeroDeRonda) {
    }

    public void setDefinicion(String definicionTexto) {
    }

    public int getNumeroDeRonda() {
        return numeroDeRonda;
    }

    public void setNumeroDeRonda(int numeroDeRonda) {
        this.numeroDeRonda = numeroDeRonda;
    }

    public String getPalabra() {
        return palabra;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public String getDefinicionTexto() {
        return definicionTexto;
    }

    public void setDefinicionTexto(String definicionTexto) {
        this.definicionTexto = definicionTexto;
    }


}
