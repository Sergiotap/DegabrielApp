package com.example.degabriel.adapter;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.degabriel.R;

import java.util.List;

public class detalleArticuloAdapter extends RecyclerView.Adapter<detalleArticuloAdapter.detalleArticuloViewHolder>{
    private List<String> data;
    private onItemClickListener mListener;


    public detalleArticuloAdapter(List<String> imagenes) { this.data=imagenes;
    }
    public void setOnItemClickListener(detalleArticuloAdapter.onItemClickListener listener) {
        this.mListener = listener;
    }
    public interface onItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public detalleArticuloViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_detalle_articulo, parent, false);
        return new detalleArticuloViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull detalleArticuloAdapter.detalleArticuloViewHolder holder, int position) {
        String item = data.get(position);
        holder.bindData(item);
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

    public class detalleArticuloViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public detalleArticuloViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.rawDetalleArticuloImagen);
        }

        public void bindData(String imageUrl) {

            Glide.with(itemView)
                    .load(imageUrl)
                    .into(imageView);
        }
    }
}
