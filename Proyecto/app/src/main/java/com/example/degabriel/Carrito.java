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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.degabriel.adapter.carritoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Carrito extends AppCompatActivity implements carritoAdapter.onItemClickListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> documentId=new ArrayList<>();
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private ImageView carritoMenu, carritoCart, carritoUser;
    private Button reservarTodos, cancelarTodos;
    private RecyclerView recy;
    private carritoAdapter adapter;
    private List<Map<String, Object>> dataList;
    private ArrayList<String> bolsos, cesta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        asignarElementos();
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
            irAMenu();
        });

        carritoCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        carritoUser.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });
        reservarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservarTodo();
            }
        });
        cancelarTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitarTodo();
            }
        });
    }
    public void asignarElementos(){
        carritoMenu =findViewById(R.id.carritoMenu);
        carritoCart =findViewById(R.id.carritoCart);
        carritoUser =findViewById(R.id.carritoUser);
        reservarTodos = findViewById(R.id.ConfirmarTodo);
        cancelarTodos = findViewById(R.id.QuitarTodo);
        recy=findViewById(R.id.CarritoRcy);
        recy.setLayoutManager(new LinearLayoutManager(this));
        dataList=new ArrayList<>();
        adapter=new carritoAdapter(dataList);
        adapter.notifyDataSetChanged();
        recy.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        mAuth= FirebaseAuth.getInstance();
        obtenerUsuario();
    }
    public void obtenerUsuario(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            sacarBolsos(user.getUid());
        }
        else {
            irALogin();
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
    public void irALogin(){
        Intent intent = new Intent(this, Login.class);
        launcher.launch(intent);
    }
    public void irAPerfil(){
        Intent intent = new Intent(this, Perfil.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
    public void sacarBolsos(String uid){
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        bolsos = (ArrayList<String>) documentSnapshot.get("Bolsos");
                        cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                        mostrarCesta();
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
    public void mostrarCesta() {
        for (String docId : cesta) {
            db.collection("Bolsos")
                    .document(docId) // Obtener el documento por su nombre
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                documentId.add(documentSnapshot.getId());
                                Map<String, Object> data = documentSnapshot.getData();
                                dataList.add(data);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "Error getting document: ", e);
                            Toast.makeText(Carrito.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onItemClick(int position) {
        String ID=documentId.get(position);

        envio(ID);
    }
    public void envio(String ID) {
        Intent intent = new Intent(this, DetalleArticulo.class);
        intent.putExtra("ID",ID);
        startActivity(intent);

    }
    public void quitarTodo(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            eliminar();
        }
        else {
            irALogin();
        }
    }
    public void reservarTodo(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            anadirABolsos();
        }
        else {
            irALogin();
        }
    }
    public void eliminar() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                        eliminarElementosCesta(uid);
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
    public void eliminarElementosCesta(String uid) {
        if (cesta != null && !cesta.isEmpty()) {
            for (String bolsosRef : cesta) {
                // Obtener el ID del documento de bolsos a partir de la referencia almacenada en el ArrayList
                String bolsosId = bolsosRef.substring(bolsosRef.lastIndexOf("/") + 1);

                // Actualizar el campo "stock" en la colección "bolsos"
                actualizarStock(bolsosId);

                // Eliminar el elemento de la cesta en la colección "Usuarios"
                db.collection("Usuarios").document(uid)
                        .update("Cesta", FieldValue.arrayRemove(bolsosRef))
                        .addOnSuccessListener(aVoid -> {
                            // Elemento eliminado con éxito
                        })
                        .addOnFailureListener(e -> {
                            // Error al eliminar el elemento
                        });
            }

            // Limpiar la lista de cesta después de eliminar los elementos
            cesta.clear();

            // Redireccionar a la actividad Carrito
        }
    }
    public void actualizarStock(String IDBolso){
        db.collection("Bolsos").document(IDBolso)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long stock = (Long) documentSnapshot.get("Stock");
                        stock++;
                        db.collection("Bolsos").document(IDBolso)
                                .update("Stock", stock)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Se ha actualizado el stock", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Carrito.this, Carrito.class);
                                    startActivity(intent);
                                    finish();
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


    public void actualizarCesta(String uid){
        Map<String, Object> updateData = new HashMap<>();
        cesta=new ArrayList<>();
        updateData.put("Cesta", cesta); // cesta es el nuevo valor que deseas asignar

        db.collection("Usuarios").document(uid)
                .update(updateData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Se ha modificado la cesta del usuario", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Carrito.this, Carrito.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "No se ha modificado la cesta del usuario", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    public void anadirABolsos(){
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        bolsos = (ArrayList<String>) documentSnapshot.get("Bolsos");
                        cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                        actualizarBolsos(uid);
                        //actualizarCesta(uid);
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
    public void actualizarBolsos(String uid){
        obtenerBolsos(uid);
    }
    public void obtenerBolsos(String uid){
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        bolsos = (ArrayList<String>) documentSnapshot.get("Bolsos");
                        modificarBolsos(bolsos, uid);
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
    public void modificarBolsos(ArrayList<String> bolsos, String uid){
        cesta = obtenerCesta(uid);
        Toast.makeText(this, cesta.toString(), Toast.LENGTH_SHORT).show();
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> bolsos = (ArrayList<String>) documentSnapshot.get("Bolsos");

                        if (bolsos == null) {
                            bolsos = new ArrayList<>();
                        }

                        bolsos.addAll(cesta);

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("Bolsos", bolsos);

                        db.collection("Usuarios").document(uid)
                                .update(updateData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(), "Se han modificado los bolsos del usuario", Toast.LENGTH_SHORT).show();
                                        actualizarCesta(uid);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "No se han modificado los bolsos del usuario", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public ArrayList<String> obtenerCesta(String uid){
        db.collection("Usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // El documento existe, se ha obtenido con éxito
                        cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                        // Acceder a los datos del documento
                        // ...
                    } else {
                        // El documento no existe
                    }
                })
                .addOnFailureListener(e -> {
                    // Error al obtener el documento
                });
        return cesta;
    }



}