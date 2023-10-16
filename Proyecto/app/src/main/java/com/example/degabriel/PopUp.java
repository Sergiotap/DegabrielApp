package com.example.degabriel;

import android.app.AlertDialog;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.degabriel.R;

public class PopUp {
    public void mostrarMensaje(String mensaje, View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext(), R.style.AlertDialogStyle);

        // Crear una vista personalizada con un RelativeLayout
        RelativeLayout customLayout = new RelativeLayout(v.getContext());

        // Crear el texto y establecer el mensaje
        TextView textView = new TextView(v.getContext());
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setText(mensaje);

        // Crear la imagen y establecer el recurso
        ImageView imageView = new ImageView(v.getContext());
        imageView.setImageResource(R.drawable.logoimg);


        // Establecer el padding en el ImageView
        int paddingInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                3,
                v.getResources().getDisplayMetrics()
        );
        imageView.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);

        // Configurar las reglas de posicionamiento para los elementos en el RelativeLayout
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textParams.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        textParams.setMarginStart(400); // Ajusta el margen según sea necesario
        textView.setLayoutParams(textParams);

        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        imageParams.addRule(RelativeLayout.CENTER_VERTICAL);
        imageParams.addRule(RelativeLayout.LEFT_OF, textView.getId());
        imageParams.setMarginStart(250);
        imageView.setLayoutParams(imageParams);

        // Agregar la imagen y el texto al RelativeLayout
        customLayout.addView(imageView);
        customLayout.addView(textView);

        // Establecer la vista personalizada en el cuadro de diálogo
        builder.setView(customLayout);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        // Centrar el mensaje horizontalmente después de que el cuadro de diálogo se muestre
        TextView messageTextView = alertDialog.findViewById(android.R.id.message);
        if (messageTextView != null) {
            messageTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        }
    }
}
