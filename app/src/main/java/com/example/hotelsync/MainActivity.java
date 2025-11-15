package com.example.hotelsync;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText TxtCedula, TxtNombre;
    RadioButton BtnHuesped, BtnEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TxtCedula = findViewById(R.id.TxtCedula);
        TxtNombre = findViewById(R.id.TxtNombre);
        BtnHuesped = findViewById(R.id.BtnHuesped);
        BtnEmpleado = findViewById(R.id.BtnEmpleado);
    }

    public void Siguiente(View view)  {
        Intent intent= new Intent(this,Registro.class);
        startActivity(intent);
    }

    public void InicioHuesped(View view)  {
        String cedula = TxtCedula.getText().toString();
        String nombre = TxtNombre.getText().toString();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese cédula y nombre", Toast.LENGTH_LONG).show();
            return;
        }

        DBGestion admin = new DBGestion(this, "BaseDatos", null, 1);
        SQLiteDatabase BaseDatos = admin.getReadableDatabase();

        String query = "SELECT * FROM huesped WHERE cedula=? AND nombre=?";
        String[] parametros = { cedula, nombre };
        try {
            var cursor = BaseDatos.rawQuery(query, parametros);
            if (cursor.moveToFirst()) {
                Intent intent = new Intent(this, ReservaActivity.class);
                startActivity(intent);

                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show();
            }
            cursor.close();
            BaseDatos.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void InicioEmpleado(View view)  {
        String cedula = TxtCedula.getText().toString();
        String nombre = TxtNombre.getText().toString();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese cédula y nombre", Toast.LENGTH_LONG).show();
            return;
        }

        DBGestion admin = new DBGestion(this, "BaseDatos", null, 1);
        SQLiteDatabase BaseDatos = admin.getReadableDatabase();

        String query = "SELECT * FROM empleado WHERE cedula=? AND nombre=?";
        String[] parametros = { cedula, nombre };
        try {
            var cursor = BaseDatos.rawQuery(query, parametros);
            if (cursor.moveToFirst()) {
                Intent intent = new Intent(this, EmpleadoActivity.class);
                startActivity(intent);

                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show();
            }
            cursor.close();
            BaseDatos.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}