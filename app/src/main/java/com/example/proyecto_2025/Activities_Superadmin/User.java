package com.example.proyecto_2025.Activities_Superadmin;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String nombre;
    private String apellidos;
    private String dni;
    private String fechaNacimiento;
    private String correo;
    private String telefono;
    private String domicilio;
    private List<String> idiomas;
    private String rol;
    private String fotoUrl;
    private boolean activo; // 🔹 Nuevo campo: estado del usuario

    public User(String id, String nombre, String apellidos, String dni, String fechaNacimiento,
                String correo, String telefono, String domicilio, List<String> idiomas,
                String rol, String fotoUrl, boolean activo) { // 🔹 Se agregó 'activo' al constructor
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.correo = correo;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.idiomas = idiomas;
        this.rol = rol;
        this.fotoUrl = fotoUrl;
        this.activo = activo;
    }

    // 🔹 Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getDni() { return dni; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getDomicilio() { return domicilio; }
    public List<String> getIdiomas() { return idiomas; }
    public String getRol() { return rol; }
    public String getFotoUrl() { return fotoUrl; }
    public boolean isActivo() { return activo; } // ✅ Getter del nuevo campo

    // 🔹 Setters
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setDni(String dni) { this.dni = dni; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDomicilio(String domicilio) { this.domicilio = domicilio; }
    public void setIdiomas(List<String> idiomas) { this.idiomas = idiomas; }
    public void setRol(String rol) { this.rol = rol; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public void setActivo(boolean activo) { this.activo = activo; } // ✅ Setter del nuevo campo
}
