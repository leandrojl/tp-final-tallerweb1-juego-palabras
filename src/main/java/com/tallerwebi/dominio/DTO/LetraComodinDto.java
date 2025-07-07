package com.tallerwebi.dominio.DTO;

public class LetraComodinDto {

    private int indice;
    private String letra;


    public LetraComodinDto(int indiceLetra, String letra) {
        this.indice = indiceLetra;
        this.letra = letra;
    }

    public int getIndice() {
        return indice;
    }

    public void setIndice(int indice) {
        this.indice = indice;
    }

    public String getLetra() {
        return letra;
    }

    public void setLetra(String letra) {
        this.letra = letra;
    }
}
