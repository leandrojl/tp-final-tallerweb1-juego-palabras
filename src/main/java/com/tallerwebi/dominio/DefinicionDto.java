package com.tallerwebi.dominio;

public class DefinicionDto {

    private int numeroDeRonda;
    private String definicionTexto;
    private String descripcion;


    public DefinicionDto() {

    }


    public DefinicionDto(int numeroDeRonda, String definicionTexto, String descripcion) {
        this.numeroDeRonda = numeroDeRonda;
        this.definicionTexto = definicionTexto;
        this.descripcion = descripcion;
    }

    public int getNumeroDeRonda() {
        return numeroDeRonda;
    }

    public String getDefinicionTexto() {
        return definicionTexto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setNumeroDeRonda(int numeroDeRonda) {
        this.numeroDeRonda = numeroDeRonda;
    }

    public void setDefinicionTexto(String definicionTexto) {
        this.definicionTexto = definicionTexto;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
