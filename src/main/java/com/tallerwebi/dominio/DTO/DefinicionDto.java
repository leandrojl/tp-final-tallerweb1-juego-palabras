package com.tallerwebi.dominio.DTO;

public class DefinicionDto {

    private int numeroDeRonda;
    private String definicionTexto;
    private String palabra;
    private int tiempo;

    public DefinicionDto(String palabra, String definicionTexto, int numeroDeRonda, int tiempo) {
        this.palabra = palabra;
        this.definicionTexto = definicionTexto;
        this.numeroDeRonda = numeroDeRonda;
        this.tiempo = tiempo;
    }

    public DefinicionDto() {
    }

    // Getters y setters
    public int getNumeroDeRonda() {
        return numeroDeRonda;
    }

    public String getDefinicionTexto() {
        return definicionTexto;
    }

    public String getPalabra() {
        return palabra;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setNumeroDeRonda(int numeroDeRonda) {
        this.numeroDeRonda = numeroDeRonda;
    }

    public void setDefinicionTexto(String definicionTexto) {
        this.definicionTexto = definicionTexto;
    }

    public void setPalabra(String palabra) {
        this.palabra = palabra;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
}
