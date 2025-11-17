package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Context;

import com.example.proyecto_2025.model.User;

import java.util.*;

/**
 * Repositorio de usuarios (admins, guías y clientes)
 */
public class UserRepository {
    private static UserRepository instance;
    private final List<User> users = new ArrayList<>();

    private UserRepository() {}

    public static UserRepository get() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    /** Carga data de demo si no hay usuarios aún */
    public void seedIfEmpty(Context ctx) {
        if (!users.isEmpty()) return;

        // 🔹 ADMINISTRADORES
        users.add(new User("a1", "Carlos", "Torres", "45678912", "1985-03-10",
                "carlos.torres@admin.com", "987654321", "Av. Los Pinos 123 - Cusco",
                Arrays.asList("ES", "EN"), "Administrador",
                "https://picsum.photos/seed/admin1/400", true));

        users.add(new User("a2", "Lucía", "Ramírez", "47890123", "1988-07-15",
                "lucia.ramirez@admin.com", "975321654", "Jr. Grau 450 - Arequipa",
                Arrays.asList("ES"), "Administrador",
                "https://picsum.photos/seed/admin2/400", false));

        users.add(new User("a3", "Miguel", "Quispe", "40256897", "1990-09-20",
                "miguel.quispe@admin.com", "986412357", "Calle Lima 340 - Lima",
                Arrays.asList("ES", "EN"), "Administrador",
                "https://picsum.photos/seed/admin3/400", true));

        // 🔹 GUÍAS
        users.add(new User("g1", "María", "Quispe", "40987512", "1992-05-01",
                "maria.quispe@guia.com", "987456321", "Av. Collasuyo 500 - Cusco",
                Arrays.asList("ES", "EN"), "Guía",
                "https://picsum.photos/seed/guide1/400", true));

        users.add(new User("g2", "José", "Huamán", "41239876", "1990-11-22",
                "jose.huaman@guia.com", "976854321", "Urb. San Blas - Cusco",
                Arrays.asList("ES", "FR"), "Guía",
                "https://picsum.photos/seed/guide2/400", false));

        users.add(new User("g3", "Ana", "Poma", "45612378", "1989-01-30",
                "ana.poma@guia.com", "985632147", "Av. Pachacútec - Cusco",
                Arrays.asList("ES", "EN", "DE"), "Guía",
                "https://picsum.photos/seed/guide3/400", true));

        // 🔹 CLIENTES
        users.add(new User("c1", "Luis", "Ccapa", "47896521", "1995-04-17",
                "luis.ccapa@cliente.com", "987321654", "Jr. Los Incas 210 - Lima",
                Arrays.asList("ES"), "Cliente",
                "https://picsum.photos/seed/client1/400", true));

        users.add(new User("c2", "Rosa", "Ñusta", "48965231", "1993-06-25",
                "rosa.nusta@cliente.com", "975654987", "Av. Tupac Amaru 700 - Arequipa",
                Arrays.asList("ES", "EN"), "Cliente",
                "https://picsum.photos/seed/client2/400", false));

        users.add(new User("c3", "David", "Huanca", "47896533", "1997-02-12",
                "david.huanca@cliente.com", "981234567", "Calle Saphy 120 - Cusco",
                Arrays.asList("ES", "PT"), "Cliente",
                "https://picsum.photos/seed/client3/400", true));
    }

    public List<User> all() {
        return new ArrayList<>(users);
    }

    public User byId(String id) {
        for (User u : users) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    // ✅ NUEVOS MÉTODOS: filtrado por rol
    public List<User> allAdmins() {
        List<User> result = new ArrayList<>();
        for (User u : users) {
            if ("Administrador".equalsIgnoreCase(u.getRol())) {
                result.add(u);
            }
        }
        return result;
    }

    public List<User> allGuias() {
        List<User> result = new ArrayList<>();
        for (User u : users) {
            if ("Guía".equalsIgnoreCase(u.getRol())) {
                result.add(u);
            }
        }
        return result;
    }

    public List<User> allClientes() {
        List<User> result = new ArrayList<>();
        for (User u : users) {
            if ("Cliente".equalsIgnoreCase(u.getRol())) {
                result.add(u);
            }
        }
        return result;
    }
}
