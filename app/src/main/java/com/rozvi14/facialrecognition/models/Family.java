package com.rozvi14.facialrecognition.models;

public class Family {
    private Integer idConocido;
    private String nombreConocido;
    private String relacionConocido;
    private String[] imagenes;

    public Family(Integer idConocido, String nombreConocido, String relacionConocido, String[] imagenes) {
        this.idConocido = idConocido;
        this.nombreConocido = nombreConocido;
        this.relacionConocido = relacionConocido;
        this.imagenes = imagenes;
    }

    public Integer getIdConocido() {
        return idConocido;
    }

    public void setIdConocido(Integer idConocido) {
        this.idConocido = idConocido;
    }

    public String getNombreConocido() {
        return nombreConocido;
    }

    public void setNombreConocido(String nombreConocido) {
        this.nombreConocido = nombreConocido;
    }

    public String getRelacionConocido() {
        return relacionConocido;
    }

    public void setRelacionConocido(String relacionConocido) {
        this.relacionConocido = relacionConocido;
    }

    public String[] getImagenes() {
        return imagenes;
    }

    public void setImagenes(String[] imagenes) {
        this.imagenes = imagenes;
    }
}
