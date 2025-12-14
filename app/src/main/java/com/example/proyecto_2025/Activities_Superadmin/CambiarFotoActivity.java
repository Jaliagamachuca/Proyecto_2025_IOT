package com.example.proyecto_2025.Activities_Superadmin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_2025.databinding.ActivityCambiarFotoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class CambiarFotoActivity extends AppCompatActivity {

    private ActivityCambiarFotoBinding binding;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCambiarFotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        binding.btnSeleccionarFoto.setOnClickListener(v -> abrirGaleria());
        binding.btnGuardarFoto.setOnClickListener(v -> subirFoto());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.imgPerfil.setImageURI(imageUri);
        }
    }

    private void subirFoto() {

        if (imageUri == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("profile_photos")
                .child(uid)
                .child("profile.jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(task ->
                        ref.getDownloadUrl().addOnSuccessListener(uri ->
                                guardarUrlEnFirestore(uri.toString())
                        )
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                );
    }

    private void guardarUrlEnFirestore(String fotoUrl) {

        Map<String, Object> data = new HashMap<>();
        data.put("photoUrl", fotoUrl);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update(data)
                .addOnSuccessListener(a -> {
                    Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar foto", Toast.LENGTH_SHORT).show()
                );
    }
}
