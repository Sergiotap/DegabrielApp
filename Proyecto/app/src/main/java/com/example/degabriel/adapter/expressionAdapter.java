package com.example.degabriel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.degabriel.R;

import java.util.List;

public class expressionAdapter extends RecyclerView.Adapter<expressionAdapter.expressionViewHolder>{
    private List<String> data;
    private onItemClickListener mListener;

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mListener = listener;
    }

    public interface onItemClickListener {
        void onItemClick(int position);
    }
    public expressionAdapter(List<String> imagenes) { this.data=imagenes;}
    @NonNull
    @Override
    public expressionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_expression_lista, parent, false);
        return new expressionViewHolder(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull expressionViewHolder holder, int position) {

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

    public class expressionViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        public expressionViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen=itemView.findViewById(R.id.expressionListaImagen);
        }

        public void bindData(String item) {
            Glide.with(itemView)
                    .load(item)
                    .into(imagen);
        }
    }
}
