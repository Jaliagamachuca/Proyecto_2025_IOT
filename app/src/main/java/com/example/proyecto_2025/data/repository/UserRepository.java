package com.example.proyecto_2025.data.repository;

import androidx.annotation.NonNull;

import com.example.proyecto_2025.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static UserRepository instance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onSuccess(T data);
        void onError(Exception e);
    }

    private UserRepository() {}

    public static UserRepository get() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    // --------- genérico por rol ----------
    public void getUsersByRole(String role, Callback<List<User>> cb) {
        db.collection("users")
                .whereEqualTo("role", role)       // "admin", "guia", "cliente"
                .get()
                .addOnSuccessListener(snaps -> {
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot d : snaps) {
                        User u = d.toObject(User.class);
                        list.add(u);
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(cb::onError);
    }

    // --------- helpers específicos ----------
    public void allAdmins(Callback<List<User>> cb) {
        getUsersByRole("admin", cb);
    }

    public void allGuias(Callback<List<User>> cb) {
        getUsersByRole("guia", cb);
    }

    public void allClientes(Callback<List<User>> cb) {
        getUsersByRole("cliente", cb);
    }

    // Si en algún momento quieres obtener TODOS los usuarios:
    public void allUsers(Callback<List<User>> cb) {
        db.collection("users").get()
                .addOnSuccessListener(snaps -> {
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot d : snaps) {
                        User u = d.toObject(User.class);
                        list.add(u);
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(cb::onError);
    }
}
