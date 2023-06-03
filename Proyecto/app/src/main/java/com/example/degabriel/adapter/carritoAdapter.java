package com.example.degabriel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.degabriel.R;

import java.util.List;
import java.util.Map;

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

    public class carritoViewHolder extends RecyclerView.ViewHolder {
        private TextView carritoModelo, carritoPrecio;
        private ImageView carritoImagen, carritoQuitar;
        private Button carritoReservar;
        public carritoViewHolder(@NonNull View itemView) {
            super(itemView);
            carritoModelo = itemView.findViewById(R.id.carritoModelo);
            carritoPrecio = itemView.findViewById(R.id.carritoPrecio);
            carritoImagen = itemView.findViewById(R.id.carritoImagen);
            //Estos son 2 que tienen click, queda pendiente ver cómo funcionan
            carritoQuitar = itemView.findViewById(R.id.carritoQuitar);
            carritoReservar = itemView.findViewById(R.id.carritoConfirmar);
        }
        public void bindData(Map<String, Object> itemData) {
            String name = (String) itemData.get("Nombre");
            carritoModelo.setText(name);
            Long precio = (long) itemData.get("Precio");
            carritoPrecio.setText(precio.toString());
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
    }
}
