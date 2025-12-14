package com.example.proyecto_2025.Activities_Usuario;

public class EmpresaTurismo {
    private String id;
    private String nombre;
    private String descripcion;
    private float rating;
    private int numeroResenias;
    private int toursDisponibles;
    private String ubicacion;
    private int logoResId; // Resource ID para el logo

    // Constructor
    public EmpresaTurismo(String id, String nombre, String descripcion, float rating,
                          int numeroResenias, int toursDisponibles, String ubicacion, int logoResId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rating = rating;
        this.numeroResenias = numeroResenias;
        this.toursDisponibles = toursDisponibles;
        this.ubicacion = ubicacion;
        this.logoResId = logoResId;
    }

    // Getters
    public String getId() { return id; }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public float getRating() { return rating; }
    public int getNumeroResenias() { return numeroResenias; }
    public int getToursDisponibles() { return toursDisponibles; }
    public String getUbicacion() { return ubicacion; }
    public int getLogoResId() { return logoResId; }

    // Setters
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setRating(float rating) { this.rating = rating; }
    public void setNumeroResenias(int numeroResenias) { this.numeroResenias = numeroResenias; }
    public void setToursDisponibles(int toursDisponibles) { this.toursDisponibles = toursDisponibles; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public void setLogoResId(int logoResId) { this.logoResId = logoResId; }

    public void setId(String id) { this.id = id; }
}