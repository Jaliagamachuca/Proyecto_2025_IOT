package com.example.proyecto_2025.data.auth;

import com.google.firebase.auth.*;
import com.google.firebase.firestore.*;
import java.util.*;
import static com.google.firebase.firestore.FieldValue.serverTimestamp;

public class AuthRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface Callback<T> { void onSuccess(T t); void onError(Exception e); }

    // LOGIN
    public void login(String email, String pass, Callback<DocumentSnapshot> cb) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(r ->
                        db.collection("users").document(r.getUser().getUid()).get()
                                .addOnSuccessListener(snap -> {
                                    if (!snap.exists()) { cb.onError(new Exception("Perfil no encontrado.")); return; }
                                    String status = snap.getString("status");
                                    if (!"active".equals(status)) { cb.onError(new Exception("Usuario deshabilitado.")); return; }
                                    cb.onSuccess(snap);
                                })
                                .addOnFailureListener(cb::onError)
                )
                .addOnFailureListener(cb::onError);
    }

    public void signUp(String name, String email, String pass, String role, Callback<Void> cb) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(r -> {
                    String uid = r.getUser().getUid();
                    Map<String,Object> m = new HashMap<>();
                    m.put("uid", uid);
                    m.put("email", email);
                    m.put("displayName", name);
                    m.put("role", role.toLowerCase());
                    m.put("status", "active");
                    m.put("companyId", null);
                    m.put("createdAt", serverTimestamp());
                    m.put("updatedAt", serverTimestamp());
                    db.collection("users").document(uid).set(m)
                            .addOnSuccessListener(v -> cb.onSuccess(null))
                            .addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    public void logout(){ auth.signOut(); }
}
