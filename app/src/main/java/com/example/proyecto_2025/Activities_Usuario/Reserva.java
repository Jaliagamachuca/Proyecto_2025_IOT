package com.example.proyecto_2025.Activities_Usuario;

public class Reserva {

    private String nombreTour;
    private String fecha;
    private String empresa;
    private String estado; // "Próxima", "Completada", "Cancelada"
    private int imagenResId; // ID de recurso (R.drawable.*)

    // Constructor vacío
    public Reserva() {
    }

    // Constructor con parámetros
    public Reserva(String nombreTour, String fecha, String empresa, String estado, int imagenResId) {
        this.nombreTour = nombreTour;
        this.fecha = fecha;
        this.empresa = empresa;
        this.estado = estado;
        this.imagenResId = imagenResId;
    }

    // Getters y Setters
    public String getNombreTour() {
        return nombreTour;
    }

    public void setNombreTour(String nombreTour) {
        this.nombreTour = nombreTour;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getImagenResId() {
        return imagenResId;
    }

    public void setImagenResId(int imagenResId) {
        this.imagenResId = imagenResId;
    }
}