package com.example.proyecto_2025.Activities_Usuario;

public class EmpresaTurismo {
    private String id;
    private String nombre;
    private String descripcion;
    private float rating;
    private int numeroResenias;
    private int toursDisponibles;
    private String ubicacion;

    private String logoUrl;   // ✅ nuevo
    private String adminId;   // ✅ nuevo
    private int logoResId;    // fallback

    public EmpresaTurismo(String id, String nombre, String descripcion, float rating,
                          int numeroResenias, int toursDisponibles, String ubicacion,
                          String logoUrl, String adminId, int logoResId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.rating = rating;
        this.numeroResenias = numeroResenias;
        this.toursDisponibles = toursDisponibles;
        this.ubicacion = ubicacion;
        this.logoUrl = logoUrl;
        this.adminId = adminId;
        this.logoResId = logoResId;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public float getRating() { return rating; }
    public int getNumeroResenias() { return numeroResenias; }
    public int getToursDisponibles() { return toursDisponibles; }
    public String getUbicacion() { return ubicacion; }

    public String getLogoUrl() { return logoUrl; }   // ✅ nuevo
    public String getAdminId() { return adminId; }   // ✅ nuevo
    public int getLogoResId() { return logoResId; }  // fallback
}
