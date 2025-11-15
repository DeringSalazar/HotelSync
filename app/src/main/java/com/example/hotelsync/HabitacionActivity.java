package com.example.hotelsync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HabitacionActivity extends AppCompatActivity {

    EditText txtCodigo, txtNumero, txtPiso, txtNombre, txtDescripcion, txtPrecio, txtCapacidad;
    Spinner spinnerEstado;
    Button btnGuardar, btnBuscar, btnActualizar, btnEliminar;
    ListView listaHabitaciones;

    DBGestion dbGestion;
    SQLiteDatabase sql;
    ArrayList<String> datos;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitacion);

        txtCodigo = findViewById(R.id.txtCodigo);
        txtNumero = findViewById(R.id.txtNumero);
        txtPiso = findViewById(R.id.txtPiso);
        txtNombre = findViewById(R.id.txtNombre);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtCapacidad = findViewById(R.id.txtCapacidad);
        spinnerEstado = findViewById(R.id.spinnerEstado);

        btnGuardar = findViewById(R.id.btnGuardarHabitacion);
        btnBuscar = findViewById(R.id.btnBuscarHabitacion);
        btnActualizar = findViewById(R.id.btnActualizarHabitacion);
        btnEliminar = findViewById(R.id.btnEliminarHabitacion);
        listaHabitaciones = findViewById(R.id.listaHabitaciones);

        dbGestion = new DBGestion(this, "BaseDatos", null, 1);
        sql = dbGestion.getWritableDatabase();
        String[] estados = {"Libre", "Ocupada"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(adapterEstados);
        btnGuardar.setOnClickListener(v -> guardarHabitacion());
        btnBuscar.setOnClickListener(v -> buscarHabitacion());
        btnActualizar.setOnClickListener(v -> actualizarHabitacion());
        btnEliminar.setOnClickListener(v -> eliminarHabitacion());
        cargarHabitaciones();
    }

    private void guardarHabitacion() {
        ContentValues valores = new ContentValues();
        valores.put("codigo", txtCodigo.getText().toString().trim());
        valores.put("numero", txtNumero.getText().toString().trim());
        valores.put("estado", spinnerEstado.getSelectedItem().toString());
        valores.put("piso", txtPiso.getText().toString().trim());
        valores.put("nombre", txtNombre.getText().toString().trim());
        valores.put("descripcion", txtDescripcion.getText().toString().trim());
        valores.put("precio_noche", txtPrecio.getText().toString().trim());
        valores.put("capacidad", txtCapacidad.getText().toString().trim());

        long r = sql.insertWithOnConflict("habitacion", null, valores, SQLiteDatabase.CONFLICT_IGNORE);
        if (r > 0)
            Toast.makeText(this, "Habitación registrada", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Código ya existe", Toast.LENGTH_SHORT).show();

        cargarHabitaciones();
    }

    private void buscarHabitacion() {
        String codigo = txtCodigo.getText().toString().trim();
        Cursor c = sql.rawQuery("SELECT * FROM habitacion WHERE codigo=?", new String[]{codigo});

        if (c.moveToFirst()) {
            txtNumero.setText(c.getString(1));
            spinnerEstado.setSelection(((ArrayAdapter<String>) spinnerEstado.getAdapter()).getPosition(c.getString(2)));
            txtPiso.setText(c.getString(3));
            txtNombre.setText(c.getString(4));
            txtDescripcion.setText(c.getString(5));
            txtPrecio.setText(c.getString(6));
            txtCapacidad.setText(c.getString(7));
            Toast.makeText(this, "Habitación encontrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No existe la habitación", Toast.LENGTH_SHORT).show();
        }
        c.close();
    }

    private void actualizarHabitacion() {
        String codigo = txtCodigo.getText().toString().trim();
        ContentValues valores = new ContentValues();
        valores.put("numero", txtNumero.getText().toString().trim());
        valores.put("estado", spinnerEstado.getSelectedItem().toString());
        valores.put("piso", txtPiso.getText().toString().trim());
        valores.put("nombre", txtNombre.getText().toString().trim());
        valores.put("descripcion", txtDescripcion.getText().toString().trim());
        valores.put("precio_noche", txtPrecio.getText().toString().trim());
        valores.put("capacidad", txtCapacidad.getText().toString().trim());

        int r = sql.update("habitacion", valores, "codigo=?", new String[]{codigo});
        if (r > 0)
            Toast.makeText(this, "Habitación actualizada", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "No existe la habitación", Toast.LENGTH_SHORT).show();

        cargarHabitaciones();
    }

    private void eliminarHabitacion() {
        String codigo = txtCodigo.getText().toString().trim();
        int r = sql.delete("habitacion", "codigo=?", new String[]{codigo});
        if (r > 0)
            Toast.makeText(this, "Habitación eliminada", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "No existe la habitación", Toast.LENGTH_SHORT).show();

        cargarHabitaciones();
    }

    private void cargarHabitaciones() {
        datos = new ArrayList<>();
        Cursor c = sql.rawQuery("SELECT * FROM habitacion", null);

        if (c.moveToFirst()) {
            do {
                datos.add(
                        "Código: " + c.getString(0) +
                                "\nNúmero: " + c.getString(1) +
                                "\nEstado: " + c.getString(2) +
                                "\nPiso: " + c.getString(3) +
                                "\nNombre: " + c.getString(4) +
                                "\nDescripción: " + c.getString(5) +
                                "\nPrecio/noche: " + c.getString(6) +
                                "\nCapacidad: " + c.getString(7)
                );
            } while (c.moveToNext());
        }
        c.close();

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listaHabitaciones.setAdapter(adaptador);
    }
}
