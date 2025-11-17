package com.example.proyecto_2025.model;

import java.util.ArrayList;
import java.util.List;

public class Empresa {

    // ================== Identidad / relaciones ==================
    public String id = null;          // id documento en "empresas"
    public String adminId = null;     // uid del admin (User.role = "admin")

    // ================== Datos básicos ==================
    public String nombre = "";
    public String descripcion = "";         // usaremos como descripcionCorta en Firestore

    // ================== Ubicación ==================
    public String direccion = "";
    public double lat = 0d;
    public double lon = 0d;                // en Firestore lo mapeamos como "lng"

    // ================== Contacto ==================
    public String correo = "";
    public String telefono = "";
    public String web = "";

    // ================== Imágenes ==================
    public List<String> imagenUris = new ArrayList<>(); // se mapea a "fotos"

    // ================== Estado lógico ==================
    // pending  : guardada pero no publicada
    // active   : publicada / visible (cuando tú lo decidas)
    // inactive : empresa deshabilitada
    // rejected : por si algún día el superadmin la rechaza
    public String status = "pending";

    // ================== Métricas (placeholder para reportes) ==================
    public double ratingPromedio = 0.0;
    public long totalValoraciones = 0;
    public long totalReservas = 0;
    public double totalIngresos = 0.0;

    // ================== Helpers ==================
    public boolean esCompleta() {
        boolean contactoOk = !correo.isEmpty() && !telefono.isEmpty();
        boolean ubicacionOk = direccion != null && !direccion.isEmpty();
        boolean fotosOk = imagenUris != null && imagenUris.size() >= 2;
        boolean descOk = descripcion != null && !descripcion.isEmpty();
        boolean nombreOk = nombre != null && !nombre.isEmpty();
        return contactoOk && ubicacionOk && fotosOk && descOk && nombreOk;
    }
}
