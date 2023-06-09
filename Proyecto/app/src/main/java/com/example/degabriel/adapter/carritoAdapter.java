package com.example.degabriel.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.degabriel.Carrito;
import com.example.degabriel.DetalleArticulo;
import com.example.degabriel.Login;
import com.example.degabriel.Menu;
import com.example.degabriel.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class carritoAdapter extends RecyclerView.Adapter<carritoAdapter.carritoViewHolder>{
    private List<Map<String, Object>> data;
    private carritoAdapter.onItemClickListener mListener;
    public carritoAdapter(List<Map<String, Object>> data) {
        this.data = data;
    }
    public void setOnItemClickListener(carritoAdapter.onItemClickListener listener) {
        this.mListener = listener;
    }
    public interface onItemClickListener {
        void onItemClick(int position);
    }
    @NonNull
    @Override
    public carritoAdapter.carritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_carrito_lista, parent, false);
        return new carritoAdapter.carritoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull carritoAdapter.carritoViewHolder holder, int position) {
        Map<String, Object> itemData = data.get(position);
        // 设置列表项视图的内容
        holder.bindData(itemData);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        mListener.onItemClick(position);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class carritoViewHolder extends RecyclerView.ViewHolder {
        private TextView carritoModelo, carritoPrecio;
        private ImageView carritoImagen, carritoQuitar;
        private Button carritoReservar;
        private FirebaseFirestore db = FirebaseFirestore.getInstance();
        private FirebaseAuth mAuth=FirebaseAuth.getInstance();
        private ArrayList<String> cesta, reservas;
        public carritoViewHolder(@NonNull View itemView) {
            super(itemView);
            carritoModelo = itemView.findViewById(R.id.carritoModelo);
            carritoPrecio = itemView.findViewById(R.id.carritoPrecio);
            carritoImagen = itemView.findViewById(R.id.carritoImagen);
            //Estos son 2 que tienen click, queda pendiente ver cómo funcionan
            carritoQuitar = itemView.findViewById(R.id.carritoQuitar);
            carritoReservar = itemView.findViewById(R.id.carritoConfirmar);
            carritoReservar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String modeloABuscar = carritoModelo.getText().toString();
                    CollectionReference bolsosCollection = db.collection("Bolsos");
                    Query query = bolsosCollection.whereEqualTo("Nombre", modeloABuscar);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    // Aquí tienes el ID del documento que coincide con el nombre
                                    // Puedes utilizar el ID como necesites
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    cambiarReserva(user, v, id);

                                    //Toast.makeText(v.getContext(), "" + id, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                Toast.makeText(v.getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            carritoQuitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String modeloABuscar = carritoModelo.getText().toString();
                    CollectionReference bolsosCollection = db.collection("Bolsos");
                    Query query = bolsosCollection.whereEqualTo("Nombre", modeloABuscar);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String id = document.getId();
                                    // Aquí tienes el ID del documento que coincide con el nombre
                                    // Puedes utilizar el ID como necesites
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    cambiarCesta(user, v, id);

                                    //Toast.makeText(v.getContext(), "" + id, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                Toast.makeText(v.getContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

        }
        public void bindData(Map<String, Object> itemData) {

            String name = (String) itemData.get("Nombre");
            carritoModelo.setText(name);
            Long precio = (long) itemData.get("Precio");
            carritoPrecio.setText(precio.toString()+" €");
            List<String> imageUrls = (List<String>) itemData.get("Imagen");
            if (imageUrls != null && imageUrls.size() > 0) {
                // Obtenga la URL de la primera imagen del arreglo
                String firstImageUrl = imageUrls.get(0);

                // Utilice Glide para cargar la imagen en el ImageView
                Glide.with(itemView)
                        .load(firstImageUrl)
                        .into(carritoImagen);
            }
        }
        public void cambiarReserva(FirebaseUser user, View v, String idBolso){
            if (user != null) {
                comprobarDatosVacios(user, v)
                        .thenAccept(vacio -> {
                            if (!vacio) {
                                reservar(idBolso, v);
                            }
                        })
                        .exceptionally(e -> {
                            // Manejar la excepción en caso de error
                            Toast.makeText(v.getContext(), "Se ha producido un error", Toast.LENGTH_SHORT).show();
                            return null;
                        });
            } else {
                Toast.makeText(v.getContext(), "No se puede reservar porque le faltan campos", Toast.LENGTH_SHORT).show();
            }
        }
        public void cambiarCesta(FirebaseUser user, View v, String idBolso)
        {
            eliminar(idBolso, v);
        }
        public CompletableFuture<Boolean> comprobarDatosVacios(FirebaseUser user, View v){
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            String uid = user.getUid();
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
                                Toast.makeText(v.getContext(), "No se puede tener reservas en un usuario al que le faltan datos", Toast.LENGTH_SHORT).show();
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
        public void reservar(String IDBolso, View v) {
            FirebaseUser user = mAuth.getCurrentUser();
            String IDUsuario = user.getUid();
            actualizarBolsos(IDUsuario, IDBolso, v);
        }
        public void eliminar(String IDBolso, View v){
            FirebaseUser user = mAuth.getCurrentUser();
            String IDUsuario = user.getUid();
            actualizarCesta(IDUsuario, IDBolso, v);
        }


        public void actualizarBolsos(String IDUsuario, String IDBolso, View v){
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
                                        actualizarCestaReserva(IDUsuario, IDBolso, v);
                                        Toast.makeText(v.getContext(), "Se ha añadido el bolso a las reservas del usuario", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al actualizar el campo "cesta"
                                        Toast.makeText(v.getContext(), "No se ha añadido el bolso a las reservas del usuario", Toast.LENGTH_SHORT).show();
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
        public void actualizarCesta(String IDUsuario, String IDBolso, View v){
            db.collection("Usuarios").document(IDUsuario)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // El documento existe, se ha obtenido con éxito
                            cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                            cesta.remove(IDBolso);
                            db.collection("Usuarios").document(IDUsuario)
                                    .update("Cesta", cesta)
                                    .addOnSuccessListener(aVoid -> {
                                        // El campo "cesta" se ha actualizado con éxito
                                        Toast.makeText(v.getContext(), "Se ha eliminado el bolso de la cesta del usuario", Toast.LENGTH_SHORT).show();
                                        actualizarStock(IDUsuario, IDBolso, v);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al actualizar el campo "cesta"
                                        Toast.makeText(v.getContext(), "No se ha eliminado el bolso de la cesta del usuario", Toast.LENGTH_SHORT).show();
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
        public void actualizarCestaReserva(String IDUsuario, String IDBolso, View v){
            db.collection("Usuarios").document(IDUsuario)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // El documento existe, se ha obtenido con éxito
                            cesta = (ArrayList<String>) documentSnapshot.get("Cesta");
                            cesta.remove(IDBolso);
                            db.collection("Usuarios").document(IDUsuario)
                                    .update("Cesta", cesta)
                                    .addOnSuccessListener(aVoid -> {
                                        // El campo "cesta" se ha actualizado con éxito
                                        Toast.makeText(v.getContext(), "Se ha movido el bolso de la cesta del usuario", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(v.getContext(), Carrito.class);
                                        v.getContext().startActivity(intent);
                                        ((Activity) v.getContext()).finish();                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al actualizar el campo "cesta"
                                        Toast.makeText(v.getContext(), "No se ha eliminado el bolso de la cesta del usuario", Toast.LENGTH_SHORT).show();
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
        public void actualizarStock(String IDUsuario, String IDBolso, View v){
            db.collection("Bolsos").document(IDBolso)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Long stock = (Long) documentSnapshot.get("Stock");
                            stock++;
                            Long finalStock = stock;
                            db.collection("Bolsos").document(IDBolso)
                                    .update("Stock", stock)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(v.getContext(), "Se ha actualizado el stock", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(v.getContext(), Carrito.class);
                                        v.getContext().startActivity(intent);
                                        ((Activity) v.getContext()).finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Error al actualizar el campo "cesta"
                                        Toast.makeText(v.getContext(), "No se ha actualizado el stock", Toast.LENGTH_SHORT).show();
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
    }
}
