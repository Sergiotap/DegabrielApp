package com.example.degabriel;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.degabriel.adapter.catalogoAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Catalogo extends AppCompatActivity implements catalogoAdapter.onItemClickListener{
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<String> documentId=new ArrayList<>();
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private ImageView catalogoMenu, catalogCart, catalogUser;
    private RecyclerView recy;
    private catalogoAdapter adapter;
    private List<Map<String, Object>> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);
        mAuth=FirebaseAuth.getInstance();
        catalogoMenu =findViewById(R.id.catalogoMenu);
        catalogCart =findViewById(R.id.catalogoCart);
        catalogUser =findViewById(R.id.catalogoPerfil);
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
                        Toast.makeText(Catalogo.this, "He vuelto", Toast.LENGTH_SHORT).show();

                    }
                });
        recy=findViewById(R.id.catalogoRcy);
        recy.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        adapter = new catalogoAdapter(dataList);
        recy.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
        //obtenerCatalogo();
        readDataFromFirestore();



        catalogoMenu.setOnClickListener(view -> {
            Intent intent = new Intent(this, Menu.class);
            startActivity(intent);
            finish();
        });

        catalogCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        catalogUser.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });
    }

    private void readDataFromFirestore() {
        db.collection("Bolsos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 处理每个文档的数据
                                documentId .add(document.getId());
                                Map<String, Object> data = document.getData();



                                // 添加数据到列表
                                dataList.add(data);
                            }
                            // 更新UI显示适配器的数据
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(Catalogo.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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