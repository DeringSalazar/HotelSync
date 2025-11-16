package com.example.hotelsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Huesped extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huesped);

    }

    public void reservaHuesped(View view)  {
        Intent intent = new Intent(this, ReservaActivity.class);
        startActivity(intent);
    }


    public void usuario(View view)  {
        Intent intent= new Intent(this,Usuarios.class);
        startActivity(intent);
    }

    public void Volver(View view)  {
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}