package com.example.degabriel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import android.view.View;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText correo, correo2, contrasenia, contrasenia2;
    private CheckBox unirse;
    private Button registro;
    private TextView iniciarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        asignarElementos();
        FirebaseApp.initializeApp(this);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobardatos();
            }
        });
        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irALogin();
            }
        });
    }
    public void asignarElementos(){
        correo=findViewById(R.id.RegistroCorreo);
        contrasenia=findViewById(R.id.RegistroContraseña);
        correo2=findViewById(R.id.RegistroCorreoConfirmar);
        contrasenia2=findViewById(R.id.RsgistroContraseñaRepetir);
        unirse=findViewById(R.id.RegistroUnirse);
        registro=findViewById(R.id.RegistroBoton);
        iniciarSesion=findViewById(R.id.RegistroIniciar);
    }

    public void comprobardatos(){

        String email1=correo.getText().toString();
        String email2=correo2.getText().toString();
        String password1=contrasenia.getText().toString();
        String password2=contrasenia2.getText().toString();
        if ((comprobarVacios(email1))||(comprobarVacios(email2))||(comprobarVacios(password1)||comprobarVacios(password2))){
            Toast.makeText(this, "Hay un campo vacío", Toast.LENGTH_SHORT).show();
        }
        else if (!email1.equals(email2)){
            Toast.makeText(this, "Los correos no coinciden", Toast.LENGTH_SHORT).show();
        }

        else{
            if (!password1.equals(password2)){
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
            else{
                registrar(email1, password1);
            }
        }

    }
    public boolean comprobarVacios(String cadena){
        boolean vacio=true;
        if (!(cadena.length() ==0)){
            vacio=false;
        }
        return vacio;
    }
    public void  registrar(String email, String password){
        mAuth=FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Registro.this, "Usuario creado con éxito", Toast.LENGTH_SHORT).show();
                            String uid = mAuth.getCurrentUser().getUid();
                            addUsuario(uid, email);

                            irAPrincipal();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Registro.this, "Se ha fallado en la creación del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void irALogin(){
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void irAPrincipal(){
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
        finish();
    }
    public void addUsuario(String uid, String email){
        Map<String, Object> userData = new HashMap<>();
        userData.put("Correo", email);
        userData.put("Apellidos", "");
        userData.put("Direccion", "");
        userData.put("Nombre", "");
        userData.put("Telefono", "");
        userData.put("Bolsos", new ArrayList<>());
        userData.put("Cesta", new ArrayList<>());
        boolean suscrito = unirse.isChecked();
        userData.put("Suscrito", suscrito);
        db.collection("Usuarios").document(uid)
                .set(userData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(Registro.this,"Se ha añadido el usuario", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Registro.this,"No se ha añadido el usuario", Toast.LENGTH_SHORT).show();
                });
    }
}