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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Noticias extends AppCompatActivity implements noticiasAdapter.onItemClickListener{
    private List<String> documentId=new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView noticiaMenu,noticiaCart,noticiaPerfil;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> launcher;
    private List<Map<String, Object>> datoLista = new ArrayList<>();;
    private List<String> http = new ArrayList<>();
    private List<String> dateStrings = new ArrayList<>();


    private Map<String, Object> data;
    private Date date;
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


        adapter = new noticiasAdapter(datoLista);
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
                        //Toast.makeText(Noticias.this, "He vuelto", Toast.LENGTH_SHORT).show();

                    }
                });

        noticiaMenu.setOnClickListener(view -> {
            irAMenu();
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
                                data = document.getData();

                                http.add((String)data.get("url"));

                                dateStrings.add((String) data.get("Fecha"));

                                // 添加数据到列表
                                datoLista.add(data);

                            }

                            List<DataObject> dataList = new ArrayList<>();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

                            for (int i = 0; i < dateStrings.size(); i++) {
                                try {
                                    date = sdf.parse(dateStrings.get(i));
                                    DataObject dataObject = new DataObject(date, documentId.get(i), http.get(i),datoLista.get(i));
                                    dataList.add(dataObject);

                                    Log.d(TAG, "Data added to dataList: " + dataObject.getData());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "Exception caught: " + e.getMessage());
                                }
                            }

                            // 使用Comparator对自定义对象列表进行排序
                            Collections.sort(dataList, new Comparator<DataObject>() {
                                @Override
                                public int compare(DataObject dataObject1, DataObject dataObject2) {
                                    return dataObject1.getDate().compareTo(dataObject2.getDate());
                                }
                            });
                            datoLista.clear();
                            // 更新排序后的数据到原始数组
                            for (int i = 0; i < dataList.size(); i++) {
                                DataObject dataObject = dataList.get(i);
                                dateStrings.set(i, sdf.format(dataObject.getDate()));
                                documentId.set(i, dataObject.getId());
                                http.set(i, dataObject.getImage());
                                datoLista.add(dataObject.getData());
                            }
                            adapter.notifyDataSetChanged();

                            Log.d(TAG, "datoLista size: " + datoLista.size());
                            Log.d(TAG, "Data added to datoLista: " + data.toString());
                            //Toast.makeText(Noticias.this, " aa " +dataList.get(0).getDateString(), Toast.LENGTH_SHORT).show();
                            // 更新UI显示适配器的数据
                            // adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Log.d(TAG, "datoLista size: " + datoLista.size());
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
    private static class DataObject {
        private Date date;
        private String id;
        private String image;

        private Map<String, Object> data;
        public DataObject(Date date, String id, String image,Map<String, Object> data) {
            this.date = date;
            this.id = id;
            this.image = image;
            this.data =data;
        }

        public Date getDate() {
            return date;
        }

        public String getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setDate(Date date) {
            this.date = date;
        }
        public String getDateString() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(date);
        }
    }
}