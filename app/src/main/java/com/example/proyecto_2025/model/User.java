package com.example.proyecto_2025.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {

    // ==========================
    // CAMPOS BASE
    // ==========================
    private String uid;
    private String displayName;
    private String email;
    private String photoUrl;
    private String phone;
    private String dni;

    private String role;         // superadmin | admin | guia | cliente
    private String status;       // active | inactive | pending | blocked | rejected

    private String companyId;

    // Evitan error: Firebase Timestamp **NO ES Serializable**
    private transient Timestamp createdAt;
    private transient Timestamp updatedAt;


    // ==========================
    // CAMPOS OPCIONALES POR COMPATIBILIDAD
    // (de tu modelo anterior, para no romper pantallas)
    // ==========================
    private String nombre;
    private String apellidos;
    private String fechaNacimiento;
    private String domicilio;


    // ==========================
    // CAMPOS GUIA
    // ==========================
    private List<String> idiomas;
    private List<String> zonaOperacion;

    private Double ratingPromedio;
    private Long totalValoraciones;
    private Long toursRealizados;


    // ==========================
    // CAMPOS CLIENTE
    // ==========================
    private Long reservasTotales;
    private transient Timestamp ultimaReservaAt;


    // ==========================
    // SUPERADMIN
    // ==========================
    private Boolean esMaster;


    // Constructor vacío (OBLIGATORIO para Firestore)
    public User() {}


    // Constructor mínimo
    public User(String uid, String displayName, String email, String role, String status) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.role = role;
        this.status = status;
    }


    // ==========================
    // GETTERS
    // ==========================
    public String getUid() { return uid; }
    public String getDisplayName() { return displayName; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }
    public String getPhone() { return phone; }
    public String getDni() { return dni; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
    public String getCompanyId() { return companyId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }

    // Compatibilidad antigua
    public String getNombre() { return nombre != null ? nombre : displayName; }
    public String getApellidos() { return apellidos; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getDomicilio() { return domicilio; }

    // Guía
    public List<String> getIdiomas() { return idiomas; }
    public List<String> getZonaOperacion() { return zonaOperacion; }
    public Double getRatingPromedio() { return ratingPromedio; }
    public Long getTotalValoraciones() { return totalValoraciones; }
    public Long getToursRealizados() { return toursRealizados; }

    // Cliente
    public Long getReservasTotales() { return reservasTotales; }
    public Timestamp getUltimaReservaAt() { return ultimaReservaAt; }

    // Superadmin
    public Boolean getEsMaster() { return esMaster; }


    // ==========================
    // SETTERS
    // ==========================
    public void setUid(String uid) { this.uid = uid; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setDni(String dni) { this.dni = dni; }
    public void setRole(String role) { this.role = role; }
    public void setStatus(String status) { this.status = status; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // Compatibilidad antigua
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }

    // Guía
    public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
    public void setZonaOperacion(List<String> zonaOperacion) { this.zonaOperacion = zonaOperacion; }
    public void setRatingPromedio(Double ratingPromedio) { this.ratingPromedio = ratingPromedio; }
    public void setTotalValoraciones(Long totalValoraciones) { this.totalValoraciones = totalValoraciones; }
    public void setToursRealizados(Long toursRealizados) { this.toursRealizados = toursRealizados; }

    // Cliente
    public void setReservasTotales(Long reservasTotales) { this.reservasTotales = reservasTotales; }
    public void setUltimaReservaAt(Timestamp ultimaReservaAt) { this.ultimaReservaAt = ultimaReservaAt; }

    // Superadmin
    public void setEsMaster(Boolean esMaster) { this.esMaster = esMaster; }


    // ==========================
    // Helpers
    // ==========================
    public boolean isActivo() {
        return "active".equalsIgnoreCase(status);
    }

    public String getNombreCompleto() {
        return displayName != null ? displayName : (nombre != null ? nombre : "");
    }
}
