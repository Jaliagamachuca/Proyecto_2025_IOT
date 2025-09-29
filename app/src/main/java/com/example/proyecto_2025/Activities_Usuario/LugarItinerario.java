package com.example.proyecto_2025.Activities_Usuario;

public class LugarItinerario {
    private String nombreLugar;
    private String hora;
    private boolean visitado;
    private boolean actual;

    public LugarItinerario(String nombreLugar, String hora, boolean visitado, boolean actual) {
        this.nombreLugar = nombreLugar;
        this.hora = hora;
        this.visitado = visitado;
        this.actual = actual;
    }

    public String getNombreLugar() {
        return nombreLugar;
    }

    public String getHora() {
        return hora;
    }

    public boolean isVisitado() {
        return visitado;
    }

    public boolean isActual() {
        return actual;
    }
}