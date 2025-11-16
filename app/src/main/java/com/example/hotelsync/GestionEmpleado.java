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
import java.util.UUID;
import androidx.appcompat.app.AppCompatActivity;

public class GestionEmpleado extends AppCompatActivity {

    EditText txtCodigoReserva, txtCedulaEmpleado, txtCedulaHuesped, txtFechaInicio, txtFechaFin, txtTotal;
    Spinner spinnerEstado;
    Button btnCrearReserva, btnConfirmar, btnCancelar, btnBuscar;
    ListView listaReservas;

    ArrayList<String> datos;
    ArrayAdapter<String> adaptador;
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
        btnCrearReserva = findViewById(R.id.btnCrearReserva);
        btnConfirmar = findViewById(R.id.btnConfirmar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnBuscar = findViewById(R.id.btnBuscar);
        listaReservas = findViewById(R.id.listaReservas);
        DBGestion admin = new DBGestion(this, "BaseDatos", null, 1);
        basedatos = admin.getWritableDatabase();
        String[] estados = {"Pendiente", "Confirmada", "Cancelada"};
        ArrayAdapter<String> adapterEstados = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spinnerEstado.setAdapter(adapterEstados);
        btnCrearReserva.setOnClickListener(v -> crearReserva());
        btnConfirmar.setOnClickListener(v -> cambiarEstado("Confirmada"));
        btnCancelar.setOnClickListener(v -> cambiarEstado("Cancelada"));
        btnBuscar.setOnClickListener(v -> buscarReserva());

        cargarReservas();
    }

    public void Anterior(View view)  {
        Intent intent= new Intent(this,EmpleadoActivity.class);
        startActivity(intent);
    }

    private void crearReserva() {
        String codigoReserva = UUID.randomUUID().toString();
        String cedulaEmpleado = txtCedulaEmpleado.getText().toString().trim();
        String cedulaHuesped = txtCedulaHuesped.getText().toString().trim();
        String fechaInicio = txtFechaInicio.getText().toString().trim();
        String fechaFin = txtFechaFin.getText().toString().trim();
        String total = txtTotal.getText().toString().trim();

        ContentValues valoresReserva = new ContentValues();
        valoresReserva.put("id_reserva", codigoReserva);
        valoresReserva.put("cedula_empleado", cedulaEmpleado);
        valoresReserva.put("codigo_habitacion", "");
        valoresReserva.put("fecha_inicio", fechaInicio);
        valoresReserva.put("fecha_fin", fechaFin);
        valoresReserva.put("Total", total);
        basedatos.insert("reserva", null, valoresReserva);

        ContentValues valoresHuesped = new ContentValues();
        valoresHuesped.put("idreserva", codigoReserva);
        valoresHuesped.put("cedula_huesped", cedulaHuesped);
        valoresHuesped.put("estado", "Pendiente");
        basedatos.insert("reserva_huesped", null, valoresHuesped);

        Toast.makeText(this, "Reserva creada", Toast.LENGTH_SHORT).show();
        cargarReservas();
    }

    private void buscarReserva() {
        String codigoReserva = txtCodigoReserva.getText().toString().trim();
        Cursor c = basedatos.rawQuery(
                "SELECT r.id_reserva, r.cedula_empleado, rh.cedula_huesped, rh.estado, r.fecha_inicio, r.fecha_fin, r.Total " +
                        "FROM reserva r INNER JOIN reserva_huesped rh ON r.id_reserva = rh.idreserva " +
                        "WHERE r.id_reserva=?",
                new String[]{codigoReserva});

        if (c.moveToFirst()) {
            txtCedulaEmpleado.setText(c.getString(1));
            txtCedulaHuesped.setText(c.getString(2));
            spinnerEstado.setSelection(((ArrayAdapter<String>) spinnerEstado.getAdapter()).getPosition(c.getString(3)));
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

        int r = basedatos.update("reserva_huesped", valores, "idreserva=? AND cedula_huesped=?", new String[]{codigoReserva, cedulaHuesped});
        if (r > 0) {
            Toast.makeText(this, "Reserva " + nuevoEstado, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No existe la reserva", Toast.LENGTH_SHORT).show();
        }

        cargarReservas();
    }


    private void cargarReservas() {
        datos = new ArrayList<>();
        Cursor c = basedatos.rawQuery(
                "SELECT r.id_reserva, r.cedula_empleado, rh.cedula_huesped, rh.estado, r.fecha_inicio, r.fecha_fin, r.Total " +
                        "FROM reserva r INNER JOIN reserva_huesped rh ON r.id_reserva = rh.idreserva",
                null);

        if (c.moveToFirst()) {
            do {
                datos.add(
                        "Código: " + c.getString(0) +
                                "\nEmpleado: " + c.getString(1) +
                                "\nHuésped: " + c.getString(2) +
                                "\nEstado: " + c.getString(3) +
                                "\nInicio: " + c.getString(4) +
                                "\nFin: " + c.getString(5) +
                                "\nTotal: " + c.getString(6)
                );
            } while (c.moveToNext());
        }
        c.close();

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        listaReservas.setAdapter(adaptador);
    }
}