package com.example.proyecto_2025.data.auth;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthRepository {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> {
        void onSuccess(T t);
        void onError(Exception e);
    }

    // Genera código si más adelante quieres verificación manual
    private String genCode() {
        int n = new Random().nextInt(900000) + 100000;
        return String.valueOf(n);
    }

    // ===============================================================
    //  NUEVO MÉTODO (RECOMENDADO)
    //  signUpWithExtraFields(email, pass, extraFields)
    // ===============================================================
    public void signUpWithExtraFields(String email,
                                      String pass,
                                      Map<String, Object> extraFields,
                                      Callback<Void> cb) {

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener((AuthResult r) -> {
                    FirebaseUser fUser = r.getUser();
                    if (fUser == null) {
                        cb.onError(new Exception("No se pudo crear usuario en Auth."));
                        return;
                    }

                    String uid = fUser.getUid();

                    // Base mínima para Firestore
                    Map<String, Object> base = new HashMap<>();
                    base.put("uid", uid);
                    base.put("createdAt", FieldValue.serverTimestamp());
                    base.put("updatedAt", FieldValue.serverTimestamp());

                    // Mezclamos base + extraFields del Activity
                    base.putAll(extraFields);

                    db.collection("users").document(uid).set(base)
                            .addOnSuccessListener(x -> cb.onSuccess(null))
                            .addOnFailureListener(cb::onError);

                })
                .addOnFailureListener(cb::onError);
    }

    // ===============================================================
    // MÉTODO ANTIGUO (solo si quieres compatibilidad con código viejo)
    // ===============================================================
    public void signUp(String name,
                       String email,
                       String pass,
                       String role,
                       Callback<Void> cb) {

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener((AuthResult r) -> {
                    FirebaseUser fUser = r.getUser();
                    if (fUser == null) {
                        cb.onError(new Exception("No se pudo crear el usuario."));
                        return;
                    }
                    String uid = fUser.getUid();

                    String status = "active";
                    if ("guia".equalsIgnoreCase(role)) status = "pending";

                    Map<String, Object> u = new HashMap<>();
                    u.put("uid", uid);
                    u.put("displayName", name);
                    u.put("email", email);
                    u.put("role", role.toLowerCase());
                    u.put("status", status);
                    u.put("dni", null);
                    u.put("phone", null);
                    u.put("photoUrl", "https://example.com/default-user.png");
                    u.put("companyId", null);
                    u.put("createdAt", FieldValue.serverTimestamp());
                    u.put("updatedAt", FieldValue.serverTimestamp());

                    db.collection("users").document(uid).set(u)
                            .addOnSuccessListener(x -> cb.onSuccess(null))
                            .addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    // ===============================================================
    // LOGIN
    // ===============================================================
    public void login(String email, String pass, Callback<DocumentSnapshot> cb) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener((AuthResult r) -> {
                    FirebaseUser fUser = auth.getCurrentUser();
                    if (fUser == null) {
                        cb.onError(new Exception("No se pudo obtener usuario."));
                        return;
                    }
                    String uid = fUser.getUid();

                    db.collection("users").document(uid).get()
                            .addOnSuccessListener(cb::onSuccess)
                            .addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    // ===============================================================
    // GET CURRENT USER / SIGN OUT
    // ===============================================================
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signOut() {
        auth.signOut();
    }
}
