package com.example.proyecto_2025.model;

import java.io.Serializable;

public class PuntoRuta implements Serializable {
    public String nombre;
    public double lat;
    public double lon;
    public String actividad;   // texto corto
    public int minutosEstimados;

    public PuntoRuta() { }

    public PuntoRuta(String nombre, double lat, double lon, String actividad, int minutos) {
        this.nombre = nombre; this.lat = lat; this.lon = lon;
        this.actividad = actividad; this.minutosEstimados = minutos;
    }
}
