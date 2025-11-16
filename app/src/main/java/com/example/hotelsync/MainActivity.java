package com.example.hotelsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void Huesped(View view) {
        Intent intent = new Intent(this, Huesped.class);
        startActivity(intent);
    }

    public void Empleado(View view) {
        Intent intent = new Intent(this, EmpleadoActivity.class);
        startActivity(intent);
    }
}