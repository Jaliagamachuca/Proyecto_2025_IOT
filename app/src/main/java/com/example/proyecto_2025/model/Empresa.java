package com.example.proyecto_2025.model;

import java.util.ArrayList;
import java.util.List;

public class Empresa {
    public String nombre = "";
    public String correo = "";
    public String telefono = "";
    public String web = "";
    public String descripcion = "";

    public String direccion = "";
    public double lat = 0d;
    public double lon = 0d;

    public List<String> imagenUris = new ArrayList<>();

    public boolean esCompleta() {
        boolean contactoOk = !correo.isEmpty() && !telefono.isEmpty();
        boolean ubicacionOk = direccion != null && !direccion.isEmpty();
        boolean fotosOk = imagenUris != null && imagenUris.size() >= 2;
        boolean descOk = descripcion != null && !descripcion.isEmpty();
        boolean nombreOk = nombre != null && !nombre.isEmpty();
        return contactoOk && ubicacionOk && fotosOk && descOk && nombreOk;
    }
}
