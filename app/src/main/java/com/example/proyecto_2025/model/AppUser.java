package com.example.proyecto_2025.model;

public class AppUser {
    public String uid, email, displayName, role, status, companyId;
    public AppUser() {}
    public AppUser(String uid, String email, String displayName, String role) {
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.role = role;
        this.status = "active";
        this.companyId = null;
    }
}
