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

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ReservaActivity extends AppCompatActivity {

    EditText txtNombre, txtApellido, txtCedula, txtInicio, txtFin, txtCantidad;
    Button btnReservar, btnEditar, btnEliminar;
    ListView lista;

    DBGestion db;
    SQLiteDatabase sql;

    Spinner spinnerHabitaciones;
    ArrayList<String> listaHabitaciones, datos;
    ArrayAdapter<String> adapterHabitaciones, adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCedula = findViewById(R.id.txtCedula);
        spinnerHabitaciones = findViewById(R.id.spinnerHabitaciones);
        txtInicio = findViewById(R.id.txtInicio);
        txtFin = findViewById(R.id.txtFin);
        txtCantidad = findViewById(R.id.txtCantidad);
        btnReservar = findViewById(R.id.btnReservar);
        lista = findViewById(R.id.lista);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);

        db = new DBGestion(this, "BaseDatos", null, 1);
        sql = db.getWritableDatabase();

        btnReservar.setOnClickListener(v -> crearReserva());
        btnEditar.setOnClickListener(v -> editarReserva());
        btnEliminar.setOnClickListener(v -> eliminarReserva());

        cargarReservas();
        cargarHabitaciones();
    }

    private void crearReserva() {

        ContentValues huesped = new ContentValues();
        huesped.put("cedula", txtCedula.getText().toString());
        huesped.put("nombre", txtNombre.getText().toString());
        huesped.put("apellido", txtApellido.getText().toString());

        sql.insertWithOnConflict("huesped", null, huesped, SQLiteDatabase.CONFLICT_IGNORE);

        String idReserva = "R" + System.currentTimeMillis();

        ContentValues reserva = new ContentValues();
        reserva.put("id_reserva", idReserva);
        reserva.put("cedula_empleado", "EMP001"); // temporal
        reserva.put("codigo_habitacion", spinnerHabitaciones.getSelectedItem().toString());
        reserva.put("fecha_inicio", txtInicio.getText().toString());
        reserva.put("fecha_fin", txtFin.getText().toString());
        reserva.put("Total", "0");

        sql.insert("reserva", null, reserva);

        int cantidad = Integer.parseInt(txtCantidad.getText().toString());

        for (int i = 0; i < cantidad; i++) {
            ContentValues rh = new ContentValues();
            rh.put("idreserva", idReserva);
            rh.put("cedula_huesped", txtCedula.getText().toString());
            rh.put("estado", "Pendiente");
            sql.insert("reserva_huesped", null, rh);
        }

        Toast.makeText(this, "Reserva creada correctamente", Toast.LENGTH_LONG).show();

        cargarReservas();
    }

    private void editarReserva() {
        String codigo = txtCedula.getText().toString().trim();

        ContentValues r = new ContentValues();
        r.put("cedula_empleado", "EMP001");
        r.put("codigo_habitacion", spinnerHabitaciones.getSelectedItem().toString());
        r.put("fecha_inicio", txtInicio.getText().toString());
        r.put("fecha_fin", txtFin.getText().toString());
        r.put("Total", "0");

        int fila1 = sql.update("reserva", r, "id_reserva=?", new String[]{codigo});

        ContentValues h = new ContentValues();
        h.put("cedula_huesped", txtCedula.getText().toString());

        int fila2 = sql.update("reserva_huesped", h, "idreserva=?", new String[]{codigo});

        if (fila1 > 0 && fila2 > 0) {
            Toast.makeText(this, "Reserva actualizada", Toast.LENGTH_SHORT).show();
            cargarReservas();
        } else {
            Toast.makeText(this, "No existe la reserva", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarReserva() {
        String codigo = txtCedula.getText().toString().trim();

        sql.delete("reserva_huesped", "idreserva=?", new String[]{codigo});
        int filas = sql.delete("reserva", "id_reserva=?", new String[]{codigo});

        if (filas > 0) {
            Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
            cargarReservas();
        } else {
            Toast.makeText(this, "No existe la reserva", Toast.LENGTH_SHORT).show();
        }
    }



    private void cargarReservas() {

        datos = new ArrayList<>();

        Cursor c = sql.rawQuery("SELECT * FROM reserva", null);

        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String habitacion = c.getString(2);
                String inicio = c.getString(3);
                String fin = c.getString(4);

                datos.add("Reserva: " + id +
                        "\nHabitación: " + habitacion +
                        "\nInicio: " + inicio +
                        "\nFin: " + fin);
            } while (c.moveToNext());
        }

        c.close();

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        lista.setAdapter(adaptador);
    }

    private void cargarHabitaciones() {
        listaHabitaciones = new ArrayList<>();
        Cursor c = sql.rawQuery(
                "SELECT codigo FROM habitacion",
                null
        );
        if (c.moveToFirst()) {
            do {
                listaHabitaciones.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        adapterHabitaciones = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listaHabitaciones
        );
        spinnerHabitaciones.setAdapter(adapterHabitaciones);
    }

}
