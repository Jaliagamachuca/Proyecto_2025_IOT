package com.example.proyecto_2025.Activities_Guia;

import java.io.Serializable;
import java.util.List;

public class Tour implements Serializable {

    // Atributos generales
    private String nombreEmpresa;
    private String nombreTour;
    private String descripcion;
    private String fechaTour;
    private String horaInicio;
    private String horaFin;
    private String nombreAdministrador;
    private String telefonoAdministrador;
    private String fotoUrl; // URL o ruta de la foto del tour

    // Estado general
    private String estadoGeneral;

    // Subestado
    private String subEstado;

    // Pago al guía
    private double pagoOfrecido;

    // NUEVOS ATRIBUTOS
    private List<String> usuarios;          // Ej: ["Juan Pérez", "Andrés Luján"]
    private List<Ubicacion> ubicaciones;    // Lista con nombre + latitud + longitud

    // Constructor vacío (Firebase necesita esto)
    public Tour() {
    }

    // Constructor completo con los nuevos campos
    public Tour(String nombreEmpresa, String nombreTour, String descripcion,
                String fechaTour, String horaInicio, String horaFin,
                String nombreAdministrador, String telefonoAdministrador,
                String fotoUrl, String estadoGeneral, String subEstado,
                double pagoOfrecido, List<String> usuarios,
                List<Ubicacion> ubicaciones) {

        this.nombreEmpresa = nombreEmpresa;
        this.nombreTour = nombreTour;
        this.descripcion = descripcion;
        this.fechaTour = fechaTour;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.nombreAdministrador = nombreAdministrador;
        this.telefonoAdministrador = telefonoAdministrador;
        this.fotoUrl = fotoUrl;
        this.estadoGeneral = estadoGeneral;
        this.subEstado = subEstado;
        this.pagoOfrecido = pagoOfrecido;
        this.usuarios = usuarios;
        this.ubicaciones = ubicaciones;
    }

    // Getters y setters
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getNombreTour() {
        return nombreTour;
    }

    public void setNombreTour(String nombreTour) {
        this.nombreTour = nombreTour;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaTour() {
        return fechaTour;
    }

    public void setFechaTour(String fechaTour) {
        this.fechaTour = fechaTour;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getNombreAdministrador() {
        return nombreAdministrador;
    }

    public void setNombreAdministrador(String nombreAdministrador) {
        this.nombreAdministrador = nombreAdministrador;
    }

    public String getTelefonoAdministrador() {
        return telefonoAdministrador;
    }

    public void setTelefonoAdministrador(String telefonoAdministrador) {
        this.telefonoAdministrador = telefonoAdministrador;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getEstadoGeneral() {
        return estadoGeneral;
    }

    public void setEstadoGeneral(String estadoGeneral) {
        this.estadoGeneral = estadoGeneral;
    }

    public String getSubEstado() {
        return subEstado;
    }

    public void setSubEstado(String subEstado) {
        this.subEstado = subEstado;
    }

    public double getPagoOfrecido() {
        return pagoOfrecido;
    }

    public void setPagoOfrecido(double pagoOfrecido) {
        this.pagoOfrecido = pagoOfrecido;
    }

    // NUEVOS getters y setters
    public List<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<String> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Ubicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<Ubicacion> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }

    // Método útil opcional
    public String getEstadoCompleto() {
        if (estadoGeneral.equalsIgnoreCase("disponible") ||
                estadoGeneral.equalsIgnoreCase("pendiente")) {
            return estadoGeneral + " (" + subEstado + ")";
        }
        return estadoGeneral;
    }
}
