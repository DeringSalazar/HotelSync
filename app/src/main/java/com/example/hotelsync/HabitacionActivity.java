package com.example.hotelsync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HabitacionActivity extends AppCompatActivity {

    EditText txtCodigo, txtNumero, txtPiso, txtNombre, txtDescripcion, txtPrecio, txtCapacidad;
    Spinner spinnerEstado;
    Button btnGuardar, btnBuscar, btnActualizar, btnEliminar;
    ListView listaHabitaciones;

    DBGestion dbGestion;
    SQLiteDatabase sql;

    Habitacion habitacionSeleccionada;
    HabitacionAdapter adapter;

    ActivityResultLauncher<Intent> editarLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                cargarHabitaciones();
            }
    );

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


        dbGestion = new DBGestion(this, "BaseDatos", null, 2);
        sql = dbGestion.getWritableDatabase();
        String[] estados = {"Suite", "Estandar", "Familiar", "Deluxe"};
        ArrayAdapter<String> adapterEstados =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(adapterEstados);
        btnGuardar.setOnClickListener(v -> guardarHabitacion());
        btnBuscar.setOnClickListener(v -> buscarHabitacion());
        btnActualizar.setOnClickListener(v -> actualizarHabitacion());
        btnEliminar.setOnClickListener(v -> eliminarHabitacion());


        listaHabitaciones.setOnItemClickListener((parent, view, position, id) -> {
            habitacionSeleccionada = (Habitacion) parent.getItemAtPosition(position);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
            txtCodigo.setText(habitacionSeleccionada.getCodigo());
            Toast.makeText(this, "Habitación seleccionada: " + habitacionSeleccionada.getCodigo(), Toast.LENGTH_SHORT).show();
        });

        btnActualizar.setOnClickListener(v -> {
            if (habitacionSeleccionada != null) {
                Intent intent = new Intent(HabitacionActivity.this, EditarHabitacionActivity.class);
                intent.putExtra("codigo", habitacionSeleccionada.getCodigo());
                editarLauncher.launch(intent);
            }
        });

        btnEliminar.setEnabled(false);
        btnActualizar.setEnabled(false);

        cargarHabitaciones();
    }

    public void Anterior(View view) {
        startActivity(new Intent(this, EmpleadoActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHabitaciones();
    }

    public void Foto(View view) {
        Intent intent = new Intent(this, FotoActivity.class);
        startActivity(intent);
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
            spinnerEstado.setSelection(
                    ((ArrayAdapter<String>) spinnerEstado.getAdapter()).getPosition(c.getString(2))
            );
            txtPiso.setText(c.getString(3));
            txtNombre.setText(c.getString(4));
            txtDescripcion.setText(c.getString(5));
            txtPrecio.setText(c.getString(6));
            txtCapacidad.setText(c.getString(7));

            btnEliminar.setEnabled(true);
            btnActualizar.setEnabled(true);

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

        if (r > 0) {
            Toast.makeText(this, "Habitación actualizada", Toast.LENGTH_SHORT).show();
            btnActualizar.setEnabled(false);
        } else {
            Toast.makeText(this, "No existe la habitación", Toast.LENGTH_SHORT).show();
        }

        cargarHabitaciones();
    }

    private void eliminarHabitacion() {
        if (habitacionSeleccionada == null) {
            Toast.makeText(this, "Seleccione una habitación", Toast.LENGTH_SHORT).show();
            return;
        }
        String codigo = habitacionSeleccionada.getCodigo();
        int r = sql.delete("habitacion", "codigo=?", new String[]{codigo});
        if (r > 0) {
            Toast.makeText(this, "Habitación eliminada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: No se pudo eliminar", Toast.LENGTH_SHORT).show();
        }
        habitacionSeleccionada = null;
        btnEliminar.setEnabled(false);
        btnActualizar.setEnabled(false);
        txtCodigo.setText("");

        cargarHabitaciones();
    }


    private void cargarHabitaciones() {
        ArrayList<Habitacion> lista = new ArrayList<>();

        Cursor c = sql.rawQuery("SELECT * FROM habitacion", null);

        if (c.moveToFirst()) {
            do {
                lista.add(new Habitacion(
                        c.getString(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6),
                        c.getString(7),
                        R.drawable.habitacion
                ));

            } while (c.moveToNext());
        }

        c.close();

        if (adapter == null) {
            adapter = new HabitacionAdapter(this, lista);
            listaHabitaciones.setAdapter(adapter);
        } else {
            adapter.actualizarLista(lista);
        }
    }
}
