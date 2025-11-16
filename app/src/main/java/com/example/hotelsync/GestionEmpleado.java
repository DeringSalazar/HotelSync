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

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class GestionEmpleado extends AppCompatActivity {

    EditText txtCodigoReserva, txtCedulaEmpleado, txtCedulaHuesped, txtFechaInicio, txtFechaFin, txtTotal;
    Spinner spinnerEstado;
    Button btnConfirmar, btnCancelar, btnBuscar;
    ListView listaReservas;

    ArrayList<Gestion> listaGestion;
    ReservasAdapter adaptador;
    SQLiteDatabase basedatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_empleado);

        txtCodigoReserva = findViewById(R.id.txtCodigoReserva);
        txtCedulaEmpleado = findViewById(R.id.txtCedulaEmpleado);
        txtCedulaHuesped = findViewById(R.id.txtCedulaHuesped);
        txtFechaInicio = findViewById(R.id.txtFechaInicio);
        txtFechaFin = findViewById(R.id.txtFechaFin);
        txtTotal = findViewById(R.id.txtTotal);
        spinnerEstado = findViewById(R.id.spinnerEstado);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnBuscar = findViewById(R.id.btnBuscar);
        listaReservas = findViewById(R.id.listaReservas);

        DBGestion admin = new DBGestion(this, "BaseDatos", null, 1);
        basedatos = admin.getWritableDatabase();

        String[] estados = {"Pendiente", "Confirmada", "Cancelada"};
        ArrayAdapter<String> adapterEstados =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(adapterEstados);

        btnConfirmar.setOnClickListener(v -> cambiarEstado("Confirmada"));
        btnCancelar.setOnClickListener(v -> cambiarEstado("Cancelada"));
        btnBuscar.setOnClickListener(v -> buscarReserva());

        cargarReservas();
    }

    public void Anterior(View view) {
        Intent intent = new Intent(this, EmpleadoActivity.class);
        startActivity(intent);
    }

    private void buscarReserva() {
        String codigoReserva = txtCodigoReserva.getText().toString().trim();

        Cursor c = basedatos.rawQuery(
                "SELECT r.id_reserva, r.cedula_empleado, rh.cedula_huesped, rh.estado, " +
                        "r.fecha_inicio, r.fecha_fin, r.total " +
                        "FROM reserva r " +
                        "INNER JOIN reserva_huesped rh ON r.id_reserva = rh.idreserva " +
                        "WHERE r.id_reserva=?",
                new String[]{codigoReserva}
        );

        if (c.moveToFirst()) {
            txtCedulaEmpleado.setText(c.getString(1));
            txtCedulaHuesped.setText(c.getString(2));

            spinnerEstado.setSelection(
                    ((ArrayAdapter<String>) spinnerEstado.getAdapter())
                            .getPosition(c.getString(3))
            );

            txtFechaInicio.setText(c.getString(4));
            txtFechaFin.setText(c.getString(5));
            txtTotal.setText(c.getString(6));

            Toast.makeText(this, "Reserva encontrada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No existe la reserva", Toast.LENGTH_SHORT).show();
        }

        c.close();
    }

    private void cambiarEstado(String nuevoEstado) {
        String codigoReserva = txtCodigoReserva.getText().toString().trim();
        String cedulaHuesped = txtCedulaHuesped.getText().toString().trim();

        ContentValues valores = new ContentValues();
        valores.put("estado", nuevoEstado);

        int r = basedatos.update(
                "reserva_huesped",
                valores,
                "idreserva=? AND cedula_huesped=?",
                new String[]{codigoReserva, cedulaHuesped}
        );

        if (r > 0) {
            Toast.makeText(this, "Reserva " + nuevoEstado, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No existe la reserva", Toast.LENGTH_SHORT).show();
        }

        cargarReservas();
    }

    private void cargarReservas() {
        listaGestion = new ArrayList<>();
        Cursor c = basedatos.rawQuery(
                "SELECT r.id_reserva, r.cedula_empleado, rh.cedula_huesped, rh.estado, " +
                        "r.fecha_inicio, r.fecha_fin, r.total " +
                        "FROM reserva r INNER JOIN reserva_huesped rh ON r.id_reserva = rh.idreserva",
                null
        );
        if (c.moveToFirst()) {
            do {
                Gestion g = new Gestion(
                        c.getString(0),
                        R.drawable.habitacion,
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getString(6)

                );
                listaGestion.add(g);
            } while (c.moveToNext());
        }
        c.close();
        adaptador = new ReservasAdapter(this, listaGestion);
        listaReservas.setAdapter(adaptador);
    }
}
