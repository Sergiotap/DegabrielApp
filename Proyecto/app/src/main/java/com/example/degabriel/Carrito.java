package com.example.degabriel;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Carrito extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private ImageView carritoMenu, carritoCart, carritoUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        asignarElementos();
        mAuth= FirebaseAuth.getInstance();
        //obtenerUsuario();
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
                        //Esto si va a una actividad
                        Toast.makeText(Carrito.this, "He vuelto", Toast.LENGTH_SHORT).show();

                    }
                });

        carritoMenu.setOnClickListener(view -> {
            Intent intent = new Intent(Carrito.this, Menu.class);
            startActivity(intent);
            finish();
        });

        carritoCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        carritoUser.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });
    }
    public void asignarElementos(){
        carritoMenu =findViewById(R.id.carritoMenu);
        carritoCart =findViewById(R.id.carritoCart);
        carritoUser =findViewById(R.id.carritoUser);
    }
    public void obtenerUsuario(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            //sacarBolsos();
        }
        else {
            //irALogin();
        }
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
    public void irAPerfil(){
        Intent intent = new Intent(this, Perfil.class);
        launcher.launch(intent);
        //finish();
    }
    public void irALogin(){
        Intent intent = new Intent(this, Login.class);
        launcher.launch(intent);
        finish();
    }
    public void irACarro(){
        Intent intent = new Intent(this, Carrito.class);
        launcher.launch(intent);
        finish();
    }



}