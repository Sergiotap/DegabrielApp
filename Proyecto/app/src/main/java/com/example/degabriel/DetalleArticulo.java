package com.example.degabriel;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.degabriel.adapter.detalleArticuloAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DetalleArticulo extends AppCompatActivity  implements detalleArticuloAdapter.onItemClickListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private ImageView detalleMenu, detalleCart, detalleUser,detalleArticulo;
    private TextView detalleNombre,detalledescrpcion,detalleprecio, detalleStock;
    private Button detalleCestaanadir, detalleReserva;
    private ActivityResultLauncher<Intent> launcher;

    private RecyclerView recy;
    private detalleArticuloAdapter adapter;
    private List<String> Imagenes= new ArrayList<>();
    private ArrayList<String> cesta, reservas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_articulo);

        detalleMenu=findViewById(R.id.detalleMenu);
        detalleCart=findViewById(R.id.detalleCart);
        detalleUser=findViewById(R.id.detallePerfil);
        detalleNombre=findViewById(R.id.detalleArticuloNombre);
        detalledescrpcion=findViewById(R.id.detalleArticuloDescripcion);
        detalleprecio=findViewById(R.id.detalleArticuloPrecio);
        detalleArticulo=findViewById(R.id.detalleArticulo);
        detalleStock=findViewById(R.id.detalleArticuloStock);
        detalleCestaanadir =findViewById(R.id.detalleArticuloAnadir);
        detalleReserva=findViewById(R.id.detalleArticuloReservar);
        recy=findViewById(R.id.detalleArticuloRecy);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recy.setLayoutManager(layoutManager);



        String ID=getIntent().getStringExtra("ID");
        readDataFromFirestore(ID);


        adapter = new detalleArticuloAdapter(Imagenes);
        recy.setAdapter(adapter);
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

        adapter.setOnItemClickListener(this);
        detalleCestaanadir.setOnClickListener(view ->{
            comprobarUsuarioCesta();
        });
        detalleReserva.setOnClickListener(view ->{
            comprobarUsuarioReserva();
        });

        detalleMenu.setOnClickListener(view -> {
            Intent intent = new Intent(DetalleArticulo.this, Menu.class);
            startActivity(intent);
            finish();
        });

        detalleCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        detalleUser.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });
    }
    private void readDataFromFirestore(String ID) {
        db.collection("Bolsos").document(ID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> data = document.getData();
                                String name =(String)data.get("Nombre");
                                String descrip =(String)data.get("Descripcion");
                                Long precio = (Long) data.get("Precio");
                                Long stock = (Long) data.get("Stock");


                                detalleNombre.setText(name);
                                detalledescrpcion.setText(descrip);
                                detalleprecio.setText(""+precio);
                                detalleStock.setText(""+stock);

                                List<String> imageUrls = (List<String>) data.get("Imagen");
                                if (imageUrls != null && imageUrls.size() > 0) {

                                    Imagenes.addAll(imageUrls);


                                    // Obtenga la URL de la primera imagen del arreglo
                                    String firstImageUrl = imageUrls.get(0);

                                    // Utilice Glide para cargar la imagen en el ImageView
                                    Glide.with(DetalleArticulo.this)
                                            .load(firstImageUrl)
                                            .into(detalleArticulo);

                                }
                                Toast.makeText(DetalleArticulo.this, "Log in correcto", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(DetalleArticulo.this, "Log in mal", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        String imagen=Imagenes.get(position);

        cambiarImagen(imagen);
    }
    public void cambiarImagen(String imagen){

        // Utilice Glide para cargar la imagen en el ImageView
        Glide.with(DetalleArticulo.this)
                .load(imagen)
                .into(detalleArticulo);
    }
    public void comprobarUsuarioCesta() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            anadirACesta();
        } else {
            irALogin();
        }
    }

    public void comprobarUsuarioReserva(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            comprobarDatosVacios(user)
                    .thenAccept(vacio -> {
                        if (!vacio) {
                            reservar();
                        }
                    })
                    .exceptionally(e -> {
                        // Manejar la excepción en caso de error
                        return null;
                    });
        } else {
            irALogin();
        }
    }
    public CompletableFuture<Boolean> comprobarDatosVacios(FirebaseUser user){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot ->  {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        String nombreObtenido=documentSnapshot.getString("Nombre");
                        String apellidosObtenido=documentSnapshot.getString("Apellidos");
                        String direccionObtenido=documentSnapshot.getString("Direccion");
                        String telefonoObtenido=documentSnapshot.getString("Telefono");
                        boolean vacio;
                        if (nombreObtenido.isEmpty() || apellidosObtenido.isEmpty() || direccionObtenido.isEmpty() || telefonoObtenido.isEmpty()) {
                            Toast.makeText(this, "No se puede tener reservas en un usuario al que le faltan datos", Toast.LENGTH_SHORT).show();
                            vacio = true;
                        } else {
                            vacio = false;
                        }
                        future.complete(vacio);
                        // Acceder a los datos del documento
                        // ...
                    } else {
                        // El documento no existe
                        future.complete(false);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error al obtener el documento
                });
        return future;
    }

    public void irALogin(){
        Intent intent = new Intent(this, Login.class);
        launcher.launch(intent);
        finish();
    }
    public void anadirACesta(){
        FirebaseUser user = mAuth.getCurrentUser();
        String IDUsuario=user.getUid();
        String IDBolso=getIntent().getStringExtra("ID");
        actualizarCesta(IDUsuario, IDBolso);
    }
    public void reservar(){
        FirebaseUser user = mAuth.getCurrentUser();
        String IDUsuario=user.getUid();
        String IDBolso=getIntent().getStringExtra("ID");
        actualizarBolsos(IDUsuario, IDBolso);
    }
    public void actualizarCesta(String IDUsuario, String IDBolso){
        db.collection("Usuarios").document(IDUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                        cesta.add(IDBolso);
                        db.collection("Usuarios").document(IDUsuario)
                                .update("Cesta", cesta)
                                .addOnSuccessListener(aVoid -> {
                                    // El campo "cesta" se ha actualizado con éxito
                                    Toast.makeText(this, "Se ha añadido el bolso a la cesta del usuario", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Error al actualizar el campo "cesta"
                                    Toast.makeText(this, "No se ha añadido el bolso a la cesta del usuario", Toast.LENGTH_SHORT).show();
                                });
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
    public void actualizarBolsos(String IDUsuario, String IDBolso){
        db.collection("Usuarios").document(IDUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        reservas = (ArrayList<String>) documentSnapshot.get("Bolsos");
                        reservas.add(IDBolso);
                        //Se debería preguntar al usuario si desea reservar, en un futuro se hará un pop up para que lo vea el usuario
                        db.collection("Usuarios").document(IDUsuario)
                                .update("Bolsos", reservas)
                                .addOnSuccessListener(aVoid -> {
                                    // El campo "cesta" se ha actualizado con éxito
                                    actualizarStock(IDUsuario, IDBolso);
                                    Toast.makeText(this, "Se ha añadido el bolso a las reservas del usuario", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Error al actualizar el campo "cesta"
                                    Toast.makeText(this, "No se ha añadido el bolso a las reservas del usuario", Toast.LENGTH_SHORT).show();
                                });
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
    public void actualizarStock(String IDUsuario, String IDBolso){
        db.collection("Bolsos").document(IDBolso)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long stock = (Long) documentSnapshot.get("Stock");
                        stock--;
                        Long finalStock = stock;
                        db.collection("Bolsos").document(IDBolso)
                                .update("Stock", stock)
                                .addOnSuccessListener(aVoid -> {
                                    detalleStock.setText(String.valueOf(finalStock));                                    // El campo "cesta" se ha actualizado con éxito
                                    Toast.makeText(this, "Se ha actualizado el stock", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Error al actualizar el campo "cesta"
                                    Toast.makeText(this, "No se ha actualizado el stock", Toast.LENGTH_SHORT).show();
                                });
                    }
                    else {
                        // El documento no existe
                    }
                })
                .addOnFailureListener(e -> {
                    // Error al obtener el documento
                });
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
    public void irACarro(){
        Intent intent = new Intent(this, Carrito.class);
        launcher.launch(intent);
        finish();
    }

}