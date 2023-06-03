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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.degabriel.adapter.noticiasAdapter;
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

public class Noticias extends AppCompatActivity implements noticiasAdapter.onItemClickListener{
    private List<String> documentId=new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView noticiaMenu,noticiaCart,noticiaPerfil;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private List<Map<String, Object>> dataList= new ArrayList<>();;
    private List<String> http = new ArrayList<>();
    private noticiasAdapter adapter;
    private RecyclerView recy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticias);
        mAuth=FirebaseAuth.getInstance();
        noticiaMenu=findViewById(R.id.noticiaMenu);
        noticiaCart=findViewById(R.id.noticiasCart);
        noticiaPerfil=findViewById(R.id.noticiasPerfil);
        recy =findViewById(R.id.noticiasRcy);
        recy.setLayoutManager(new LinearLayoutManager(this));


        adapter = new noticiasAdapter(dataList);
        recy.setAdapter(adapter);

        adapter.setOnItemClickListener(this);

        readDataFromFirestore();
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
                        Toast.makeText(Noticias.this, "He vuelto", Toast.LENGTH_SHORT).show();

                    }
                });

        noticiaMenu.setOnClickListener(view -> {
            Intent intent = new Intent(Noticias.this, Menu.class);
            startActivity(intent);
            finish();
        });

        noticiaCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        noticiaPerfil.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });

    }
    private void readDataFromFirestore() {
        db.collection("Noticias")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 处理每个文档的数据
                                documentId .add(document.getId());
                                Map<String, Object> data = document.getData();

                                http.add((String)data.get("url"));

                                // 添加数据到列表
                                dataList.add(data);

                            }

                            // 更新UI显示适配器的数据
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(Noticias.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        String HTTP=http.get(position);

        envia(HTTP);
    }
    public void envia(String http){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(http));
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