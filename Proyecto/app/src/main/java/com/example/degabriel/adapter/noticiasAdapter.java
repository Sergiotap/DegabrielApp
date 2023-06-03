package com.example.degabriel.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.degabriel.R;

import java.util.List;
import java.util.Map;

public class noticiasAdapter extends RecyclerView.Adapter<noticiasAdapter.noticiasViewHolder>{
    private List<Map<String, Object>> data;
    private onItemClickListener mListener;
    public noticiasAdapter(List<Map<String, Object>> data) {
        this.data = data;
    }



    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
    @NonNull
    @Override
    public noticiasAdapter.noticiasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_noticia_lista, parent, false);
        return new noticiasViewHolder(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull noticiasAdapter.noticiasViewHolder holder, int position) {
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

    public class noticiasViewHolder extends RecyclerView.ViewHolder {
        TextView titulo,fecha;
        ImageView imageView;
        public noticiasViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo=itemView.findViewById(R.id.noticiasListaTitulo);
            fecha=itemView.findViewById(R.id.noticiasListaFEcha);
            imageView=itemView.findViewById(R.id.noticiaListaImagen);
        }

        public void bindData(Map<String, Object> itemData) {
            String name = (String) itemData.get("Nombre");
            titulo.setText(name);
            String FECHA = (String) itemData.get("Fecha");
            fecha.setText(FECHA);
            List<String> imageUrls = (List<String>) itemData.get("Imagen");
            if (imageUrls != null && imageUrls.size() > 0) {
                // Obtenga la URL de la primera imagen del arreglo


                String firstImageUrl = imageUrls.get(0);

                // Utilice Glide para cargar la imagen en el ImageView
                Glide.with(itemView)
                        .load(firstImageUrl)
                        .into(imageView);
            }
        }
    }
}
