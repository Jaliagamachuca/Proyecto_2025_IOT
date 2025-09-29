package com.example.proyecto_2025.Activities_Usuario;

public class TourRecomendado {
    private String nombre;
    private String empresa;
    private String precio;
    private float rating;
    private String duracion;
    private int imagenResId;

    public TourRecomendado(String nombre, String empresa, String precio,
                           float rating, String duracion, int imagenResId) {
        this.nombre = nombre;
        this.empresa = empresa;
        this.precio = precio;
        this.rating = rating;
        this.duracion = duracion;
        this.imagenResId = imagenResId;
    }

    // Getters
    public String getNombre() { return nombre; }
    public String getEmpresa() { return empresa; }
    public String getPrecio() { return precio; }
    public float getRating() { return rating; }
    public String getDuracion() { return duracion; }
    public int getImagenResId() { return imagenResId; }
}