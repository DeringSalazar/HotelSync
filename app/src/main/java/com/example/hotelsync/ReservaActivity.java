package com.example.hotelsync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ReservaActivity extends AppCompatActivity {

    EditText txtNombre, txtApellido, txtCedula, txtHabitacion, txtInicio, txtFin, txtCantidad;
    Button btnReservar;
    ListView lista;

    DBGestion db;
    SQLiteDatabase sql;

    ArrayList<String> datos;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        txtCedula = findViewById(R.id.txtCedula);
        txtHabitacion = findViewById(R.id.txtHabitacion);
        txtInicio = findViewById(R.id.txtInicio);
        txtFin = findViewById(R.id.txtFin);
        txtCantidad = findViewById(R.id.txtCantidad);
        btnReservar = findViewById(R.id.btnReservar);
        lista = findViewById(R.id.lista);

        db = new DBGestion(this, "hotel.db", null, 1);
        sql = db.getWritableDatabase();

        btnReservar.setOnClickListener(v -> crearReserva());

        cargarReservas();
    }

    private void crearReserva() {

        // 1️⃣ Guardar huésped si no existe
        ContentValues huesped = new ContentValues();
        huesped.put("cedula", txtCedula.getText().toString());
        huesped.put("nombre", txtNombre.getText().toString());
        huesped.put("apellido", txtApellido.getText().toString());

        sql.insertWithOnConflict("huesped", null, huesped, SQLiteDatabase.CONFLICT_IGNORE);

        // 2️⃣ Insertar reserva
        String idReserva = "R" + System.currentTimeMillis();

        ContentValues reserva = new ContentValues();
        reserva.put("id_reserva", idReserva);
        reserva.put("cedula_empleado", "EMP001"); // temporal
        reserva.put("codigo_habitacion", txtHabitacion.getText().toString());
        reserva.put("fecha_inicio", txtInicio.getText().toString());
        reserva.put("fecha_fin", txtFin.getText().toString());
        reserva.put("Total", "0");

        sql.insert("reserva", null, reserva);

        // 3️⃣ Insertar huéspedes según cantidad
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
}
