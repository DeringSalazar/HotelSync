package com.example.hotelsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class EmpleadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empleado);

    }

    public void Habitacion(View view)  {
        Intent intent= new Intent(this,HabitacionActivity.class);
        startActivity(intent);
    }

    public void usuarioempleado(View view)  {
        Intent intent= new Intent(this,Usuarios.class);
        intent.putExtra("tipo", "empleado");
        startActivity(intent);
    }

    public void CreacionUbicacion(View view)  {
        Intent intent= new Intent(this, UbicacionActivity.class);
        startActivity(intent);
    }

    public void VerUbicaciones (View view) {
        Intent intent = new Intent(this, ListaUbicacionesActivity.class);
        startActivity(intent);
    }

    public void Reserva(View view)  {
        Intent intent= new Intent(this,GestionEmpleado.class);
        startActivity(intent);
    }

    public void Volver(View view)  {
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}