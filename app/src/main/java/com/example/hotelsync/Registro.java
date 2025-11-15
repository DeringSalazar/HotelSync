package com.example.hotelsync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Registro extends AppCompatActivity {

    EditText TxtCedula, TxtNombre, TxtApellido, TxtTelefono, TxtCorreo;
    RadioButton BtnHuesped, BtnEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        TxtCedula = findViewById(R.id.TxtCedula);
        TxtNombre = findViewById(R.id.TxtNombre);
        TxtApellido = findViewById(R.id.TxtApellido);
        TxtTelefono = findViewById(R.id.TxtTelefono);
        TxtCorreo = findViewById(R.id.TxtCorreo);
        BtnEmpleado = findViewById(R.id.BtnEmpleado);
        BtnHuesped = findViewById(R.id.BtnHuesped);
    }

    public void Anterior(View view)  {
        Intent intent= new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void GuardarEmpleado(View view) {
        DBGestion admin = new DBGestion(this, "BaseDatos", null, 1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        String cedula, nombre, apellido, telefono, correo;

        cedula = TxtCedula.getText().toString();
        nombre = TxtNombre.getText().toString();
        apellido = TxtApellido.getText().toString();
        telefono = TxtTelefono.getText().toString();
        correo = TxtCorreo.getText().toString();

        if (!cedula.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty() && !telefono.isEmpty() && !correo.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("cedula", cedula);
            registro.put("nombre", nombre);
            registro.put("apellido", apellido);
            registro.put("telefono", telefono);
            registro.put("correo", correo);
            BaseDatos.insert("empleado", null, registro);
            BaseDatos.close();
            TxtCedula.setText("");
            TxtNombre.setText("");
            TxtApellido.setText("");
            TxtTelefono.setText("");
            TxtCorreo.setText("");
            Toast.makeText(getApplicationContext(), "Los datos se insertaron correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Faltan Datos", Toast.LENGTH_LONG).show();
        }
    }
    public void GuardarHuesped(View view) {
        DBGestion admin = new DBGestion(this, "BaseDatos", null, 1);
        SQLiteDatabase BaseDatos = admin.getWritableDatabase();

        String cedula, nombre, apellido, telefono, correo;

        cedula = TxtCedula.getText().toString();
        nombre = TxtNombre.getText().toString();
        apellido = TxtApellido.getText().toString();
        telefono = TxtTelefono.getText().toString();
        correo = TxtCorreo.getText().toString();

        if (!cedula.isEmpty() && !nombre.isEmpty() && !apellido.isEmpty() && !telefono.isEmpty() && !correo.isEmpty()) {
            ContentValues registro = new ContentValues();
            registro.put("cedula", cedula);
            registro.put("nombre", nombre);
            registro.put("apellido", apellido);
            registro.put("telefono", telefono);
            registro.put("correo", correo);
            BaseDatos.insert("huesped", null, registro);
            BaseDatos.close();
            TxtCedula.setText("");
            TxtNombre.setText("");
            TxtApellido.setText("");
            TxtTelefono.setText("");
            TxtCorreo.setText("");
            Toast.makeText(getApplicationContext(), "Los datos se insertaron correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Faltan Datos", Toast.LENGTH_LONG).show();
        }
    }
}