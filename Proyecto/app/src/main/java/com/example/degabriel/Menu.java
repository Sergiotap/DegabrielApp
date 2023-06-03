package com.example.degabriel;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Menu extends AppCompatActivity {
    private Button noticias,catalogo,expression,manfiesto;
    private ImageView menuMenu,menuCart,menuPerfil;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private String url = "https://www.degabriel-official.com/en/manifest/"; //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuMenu=findViewById(R.id.menuMenu);
        menuCart=findViewById(R.id.menuCart);
        menuPerfil=findViewById(R.id.menuPerfil);
        noticias=findViewById(R.id.menuNoticias);
        catalogo=findViewById(R.id.menuCatalogo);
        expression=findViewById(R.id.menuExpression);
        manfiesto=findViewById(R.id.menuManifiesto);
        mAuth= FirebaseAuth.getInstance();
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
                        Toast.makeText(Menu.this, "He vuelto", Toast.LENGTH_SHORT).show();

                    }
                });

        menuMenu.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, Menu.class);
            startActivity(intent);
            finish();
        });

        menuCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        menuPerfil.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });

        noticias.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, Noticias.class);
            startActivity(intent);
            finish();
        });

        catalogo.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, Catalogo.class);
            startActivity(intent);
            finish();
        });

        expression.setOnClickListener(view -> {
            Intent intent = new Intent(Menu.this, Expression.class);
            startActivity(intent);
            finish();
        });

        manfiesto.setOnClickListener(view -> {
            openWebsite();
        });
    }
    private void openWebsite() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
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