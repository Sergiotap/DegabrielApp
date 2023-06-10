package com.example.degabriel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private EditText loginCorreo, loginPassword;
    private Button botonLogin;
    private TextView registro;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        asignarElementos();
        FirebaseApp.initializeApp(this);
        botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comprobarDatos();
                //irAPrincipal();
            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irARegistro();
            }
        });
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        // Aquí puedes manejar el resultado de la actividad
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // El resultado fue exitoso
                            // Realiza las acciones necesarias
                            Intent data = result.getData();
                            // Manejar los datos devueltos por la actividad
                        } else {
                            // El resultado no fue exitoso o fue cancelado
                            // Realiza las acciones necesarias
                        }
                    }
                });
    }
    public void asignarElementos(){
        loginCorreo = findViewById(R.id.loginCorreo);
        loginPassword = findViewById(R.id.loginContra);

        botonLogin = findViewById(R.id.loginBt);
        registro = findViewById(R.id.loginRegistrarse);
    }
    public void comprobarDatos(){
        String email, pass;
        email=loginCorreo.getText().toString();
        pass=loginPassword.getText().toString();
        if (email.length()<1||pass.length()<1){
            Toast.makeText(this, "Hay campos vacíos", Toast.LENGTH_SHORT).show();
        }
        else {
            Iniciar(email, pass);
        }
    }
    public void Iniciar(String email, String pass){
        mAuth=FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this, "Log in correcto", Toast.LENGTH_SHORT).show();
                            irAPrincipal();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Las credenciales introducidas no coinciden", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void irARegistro(){
        Intent intent = new Intent(this, Registro.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void irAPrincipal(){
        Intent intent = new Intent(this, Menu.class);
        launcher.launch(intent);
        finish();
    }


}