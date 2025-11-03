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

    // 🔸 si luego quieres volver al código, ya tienes este generador
    private String genCode() {
        int n = new Random().nextInt(900000) + 100000; // 100000..999999
        return String.valueOf(n);
    }

    /**
     * REGISTRO de cliente/guia
     * Versión SIMPLE: crea el usuario en Auth y en Firestore, y queda listo para loguear.
     * - cliente: status=active
     * - guia:    status=pending (hasta que lo apruebe un admin/superadmin)
     */
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

                    // status según rol
                    String status = "active";
                    if ("guia".equalsIgnoreCase(role)) {
                        status = "pending";
                    }

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

                    // 🔻🔻🔻 FLUJO REAL CON CÓDIGO / EMAIL (COMENTADO)
                    // String code = genCode();
                    // u.put("emailVerified", false);
                    // u.put("verificationCode", code);
                    // u.put("verificationRequired", true);
                    // fUser.sendEmailVerification(); // correo de Firebase (link)
                    // 🔺🔺🔺

                    db.collection("users").document(uid).set(u)
                            .addOnSuccessListener(x -> cb.onSuccess(null))
                            .addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    /**
     * LOGIN → devuelve el doc de Firestore del usuario
     */
    public void login(String email, String pass, Callback<DocumentSnapshot> cb) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener((AuthResult r) -> {
                    FirebaseUser fUser = auth.getCurrentUser();
                    if (fUser == null) {
                        cb.onError(new Exception("No se encontró el usuario."));
                        return;
                    }
                    String uid = fUser.getUid();
                    db.collection("users").document(uid).get()
                            .addOnSuccessListener(cb::onSuccess)
                            .addOnFailureListener(cb::onError);
                })
                .addOnFailureListener(cb::onError);
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signOut() {
        auth.signOut();
    }
}
