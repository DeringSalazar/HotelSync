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
        // intent.putExtra("texto",TxtUno.getText().toString());
        startActivity(intent);
    }

    public void IniciarSesion(View view) {
        DBGestion admin = new DBGestion(this, "BaseDatos", null, 1);
        SQLiteDatabase BaseDatos = admin.getReadableDatabase();

        String cedula = TxtCedula.getText().toString().trim();
        String nombre = TxtNombre.getText().toString().trim();

        if (cedula.isEmpty() || nombre.isEmpty()) {
            Toast.makeText(this, "Ingrese todos los datos", Toast.LENGTH_LONG).show();
            return;
        }

        String rolSeleccionado = "";
        if (BtnEmpleado.isChecked()) {
            rolSeleccionado = "empleado";
        } else if (BtnHuesped.isChecked()) {
            rolSeleccionado = "huesped";
        } else {
            Toast.makeText(this, "Seleccione un rol válido", Toast.LENGTH_LONG).show();
            return;
        }

        // ✅ Espacios correctos y solo 2 parámetros
        String query = "SELECT rol FROM " + rolSeleccionado + " WHERE cedula = ? AND nombre = ?";
        String[] parametros = { cedula, nombre };

        Cursor cursor = BaseDatos.rawQuery(query, parametros);

        if (cursor.moveToFirst()) {
            String rolReal = cursor.getString(0);

            // Ya no necesitas esta validación porque consultaste la tabla correcta
            // pero la dejo por si acaso
            if (!rolReal.equals(rolSeleccionado)) {
                Toast.makeText(this, "El rol seleccionado no coincide", Toast.LENGTH_LONG).show();
                cursor.close();
                BaseDatos.close();
                return;
            }

            Intent intent;
            if (rolReal.equals("empleado")) {
                intent = new Intent(this, EmpleadoActivity.class);
            } else {
                intent = new Intent(this, ReservaActivity.class);
            }

            startActivity(intent);
            Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_LONG).show();
        }

        cursor.close();
        BaseDatos.close();
    }
}