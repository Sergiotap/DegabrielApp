package com.example.degabriel;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Perfil extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText nombre, apellidos, correo, direccion, telefono;
    private ImageView perfilMenu,perfilCart,perfilPerfil;
    Button guardar;
    TextView cerrarSesion;
    boolean suscrito;
    ArrayList<String> bolsos, cesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        mAuth=FirebaseAuth.getInstance();
        asignarElementos();
        obtenerUsuario();
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarDatos();
            }
        });
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
        perfilMenu.setOnClickListener(view -> {
            irAMenu();
        });

        perfilCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        perfilPerfil.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Aquí puedes manejar el resultado de la actividad
                        /*if (result.getResultCode() == Activity.RESULT_OK) {
                            // El resultado fue exitoso
                            // Realiza las acciones necesarias
                            Intent data = result.getData();
                            // Manejar los datos devueltos por la actividad
                        } else {
                            // El resultado no fue exitoso o fue cancelado
                            // Realiza las acciones necesarias
                        }*/
                    }
                });
    }
    public void asignarElementos(){
        nombre=findViewById(R.id.PerfilNombre);
        apellidos=findViewById(R.id.PerfilApellidos);
        correo=findViewById(R.id.PerfilCorreo);
        direccion=findViewById(R.id.PerfilDireccion);
        telefono=findViewById(R.id.PerfilTelefono);
        guardar=findViewById(R.id.PerfilGuardar);
        cerrarSesion=findViewById(R.id.PerfilCerrar);
        perfilMenu=findViewById(R.id.imageMenuPerfil);
        perfilPerfil=findViewById(R.id.imagePerfilPerfil);
        perfilCart=findViewById(R.id.imagePerfilCart);
        //perfilMenu=findViewById()
    }
    public void obtenerUsuario(){
        //Toast.makeText(this, mAuth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
        correo.setText(mAuth.getCurrentUser().getEmail());
        correo.setEnabled(false);
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        String nombreObtenido=documentSnapshot.getString("Nombre");
                        String apellidosObtenido=documentSnapshot.getString("Apellidos");
                        String direccionObtenido=documentSnapshot.getString("Direccion");
                        String telefonoObtenido=documentSnapshot.getString("Telefono");
                        suscrito= (boolean) documentSnapshot.get("Suscrito");
                        bolsos = (ArrayList<String>) documentSnapshot.get("Bolsos");
                        cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                        nombre.setText(nombreObtenido);
                        apellidos.setText(apellidosObtenido);
                        direccion.setText(direccionObtenido);
                        telefono.setText(telefonoObtenido);
                        // Acceder a los datos del documento
                        // ...
                    } else {
                        // El documento no existe
                    }
                })
                .addOnFailureListener(e -> {
                    // Error al obtener el documento
                });
    }
    public void cambiarDatos(){
        String uid = mAuth.getCurrentUser().getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("Apellidos", apellidos.getText().toString());
        userData.put("Direccion", direccion.getText().toString());
        userData.put("Nombre", nombre.getText().toString());
        userData.put("Telefono", telefono.getText().toString());
        userData.put("Correo", correo.getText().toString());
        userData.put("Bolsos", bolsos);
        userData.put("Cesta", cesta);
        userData.put("Suscrito", suscrito);
        db.collection("Usuarios").document(uid)
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this,"Se ha modificado el usuario", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Manejar errores aquí
                    Toast.makeText(this,"No se ha modificado el usuario", Toast.LENGTH_SHORT).show();
                });
    }
    public void cerrarSesion(){
        mAuth.signOut();
        irAPrincipal();
    }
    public void irAPrincipal(){
        Intent intent = new Intent(this, WelcomeActivity.class);
        launcher.launch(intent);
        finish();
    }
    public void comprobarLoginPerfil(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            irAPerfil();
        }
        else {
            irALogin();
        }
    }
    public void comprobarLoginCarro(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            irACarro();
        }
        else {
            irALogin();
        }
    }
    public void irALogin(){
        Intent intent = new Intent(this, Login.class);
        launcher.launch(intent);
    }
    public void irAPerfil(){
        Intent intent = new Intent(this, Perfil.class);
        startActivity(intent);
        finish();
    }
    public void irACarro(){
        Intent intent = new Intent(this, Carrito.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void irAMenu(){
        Intent intent = new Intent(this, Menu.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}