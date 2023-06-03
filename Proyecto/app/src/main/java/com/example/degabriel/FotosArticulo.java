package com.example.degabriel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class FotosArticulo extends AppCompatActivity {
    ImageView imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos_articulo);
        imagen=findViewById(R.id.fotosArticulosImagen);

        String ID=getIntent().getStringExtra("http");

        Glide.with(this)
                .load(ID)
                .into(imagen);
    }
}