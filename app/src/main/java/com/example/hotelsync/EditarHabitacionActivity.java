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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditarHabitacionActivity extends AppCompatActivity {

    EditText txtCodigo, txtNumero, txtPiso, txtNombre, txtDescripcion, txtPrecio, txtCapacidad;
    Spinner spinnerEstado;
    Button btnGuardar, btnRegresar;

    DBGestion dbGestion;
    SQLiteDatabase sql;

    String codigoHabitacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_habitacion);

        txtCodigo = findViewById(R.id.txtCodigoEditar);
        txtNumero = findViewById(R.id.txtNumeroEditar);
        txtPiso = findViewById(R.id.txtPisoEditar);
        txtNombre = findViewById(R.id.txtNombreEditar);
        txtDescripcion = findViewById(R.id.txtDescripcionEditar);
        txtPrecio = findViewById(R.id.txtPrecioEditar);
        txtCapacidad = findViewById(R.id.txtCapacidadEditar);
        spinnerEstado = findViewById(R.id.spinnerEstadoEditar);

        btnGuardar = findViewById(R.id.btnGuardarEditar);
        btnRegresar = findViewById(R.id.btnRegresarEditar);

        dbGestion = new DBGestion(this, "BaseDatos", null, 2);
        sql = dbGestion.getWritableDatabase();

        String[] estados = {"Suite", "Estandar", "Familiar", "Deluxe"};
        ArrayAdapter<String> adapterEstados =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(adapterEstados);

        Intent intent = getIntent();
        codigoHabitacion = intent.getStringExtra("codigo");
        if (codigoHabitacion != null) {
            cargarHabitacion(codigoHabitacion);
        }

        btnGuardar.setOnClickListener(v -> guardarCambios());
    }

    public void Volver(View view)  {
        Intent intent = new Intent(this, HabitacionActivity.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    private void cargarHabitacion(String codigo) {
        Cursor c = sql.rawQuery("SELECT * FROM habitacion WHERE codigo=?", new String[]{codigo});
        if (c.moveToFirst()) {
            txtCodigo.setText(c.getString(0));
            txtCodigo.setEnabled(false);
            txtNumero.setText(c.getString(1));
            spinnerEstado.setSelection(
                    ((ArrayAdapter<String>) spinnerEstado.getAdapter()).getPosition(c.getString(2))
            );
            txtPiso.setText(c.getString(3));
            txtNombre.setText(c.getString(4));
            txtDescripcion.setText(c.getString(5));
            txtPrecio.setText(c.getString(6));
            txtCapacidad.setText(c.getString(7));
        }
        c.close();
    }

    private void guardarCambios() {
        ContentValues valores = new ContentValues();
        valores.put("numero", txtNumero.getText().toString().trim());
        valores.put("estado", spinnerEstado.getSelectedItem().toString());
        valores.put("piso", txtPiso.getText().toString().trim());
        valores.put("nombre", txtNombre.getText().toString().trim());
        valores.put("descripcion", txtDescripcion.getText().toString().trim());
        valores.put("precio_noche", txtPrecio.getText().toString().trim());
        valores.put("capacidad", txtCapacidad.getText().toString().trim());

        int r = sql.update("habitacion", valores, "codigo=?", new String[]{codigoHabitacion});

        if (r > 0) {
            Toast.makeText(this, "Habitación actualizada correctamente", Toast.LENGTH_SHORT).show();
            cargarHabitacion(codigoHabitacion);
            setResult(RESULT_OK);
        } else {
            Toast.makeText(this, "Error al actualizar la habitación", Toast.LENGTH_SHORT).show();
        }
    }
}
