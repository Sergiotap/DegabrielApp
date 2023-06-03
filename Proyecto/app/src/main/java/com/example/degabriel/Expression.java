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
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.degabriel.adapter.catalogoAdapter;
import com.example.degabriel.adapter.expressionAdapter;
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

public class Expression extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private expressionAdapter adapter1;

    private expressionAdapter adapter2;
    private ImageView expMenu,expCart,expPerfil;
    RecyclerView recy1,recy2;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private List<String> ImagenesAll =new ArrayList<>();
    private List<String> Imagenes1= new ArrayList<>();
    private List<String> Imagenes2= new ArrayList<>();
    private String http="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expression);
        mAuth=FirebaseAuth.getInstance();
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
                        Toast.makeText(Expression.this, "He vuelto", Toast.LENGTH_SHORT).show();

                    }
                });
        expMenu=findViewById(R.id.expMenu);
        expCart=findViewById(R.id.expCart);
        expPerfil=findViewById(R.id.expPerfil);

        recy1=findViewById(R.id.expressionRecy1);
        recy1.setLayoutManager(new LinearLayoutManager(this));
        recy2=findViewById(R.id.expressionRcy2);
        recy2.setLayoutManager(new LinearLayoutManager(this));

        adapter1 = new expressionAdapter(Imagenes1);
        recy1.setAdapter(adapter1);
        adapter2 = new expressionAdapter(Imagenes2);
        recy2.setAdapter(adapter2);

        readDataFromFirestore();

        adapter1.setOnItemClickListener(new expressionAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 处理 RecyclerView1 的点击事件
                http=Imagenes1.get(position);
                envio(http);

            }
        });
        adapter2.setOnItemClickListener(new expressionAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // 处理 RecyclerView1 的点击事件
                http=Imagenes2.get(position);
                envio(http);

            }
        });


        expMenu.setOnClickListener(view -> {
            Intent intent = new Intent(this, Menu.class);
            startActivity(intent);
            finish();
        });

        expCart.setOnClickListener(view -> {
            comprobarLoginCarro();
        });

        expPerfil.setOnClickListener(view -> {
            comprobarLoginPerfil();
        });
    }
    private void readDataFromFirestore() {
        db.collection("Expression")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 处理每个文档的数据

                                Map<String, Object> data = document.getData();

                                ImagenesAll = (List<String>) data.get("Imagen");
                                if (ImagenesAll != null && ImagenesAll.size() > 0) {
                                    for(int i=0;i<ImagenesAll.size();i++){
                                        if(i%2==0){
                                            Imagenes1.add(ImagenesAll.get(i));
                                        }else{
                                            Imagenes2.add(ImagenesAll.get(i));
                                        }
                                    }

                                }
                                // 添加数据到列表

                            }
                            // 更新UI显示适配器的数据
                            adapter1.notifyDataSetChanged();
                            adapter2.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(Expression.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void envio(String ID) {
        Intent intent = new Intent(this, FotosArticulo.class);
        intent.putExtra("http",ID);
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