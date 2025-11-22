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

    EditText txtNombre, txtApellido, txtInicio, txtFin, txtCantidad;
    Button btnReservar, btnEditar, btnEliminar, btnRegresar;
    ListView lista;

    DBGestion db;
    SQLiteDatabase sql;

    Spinner spinnerHabitaciones, spinnerCedulas;
    ArrayList<String> listaHabitaciones, listaCedulas;
    ArrayAdapter<String> adapterHabitaciones, adapterCedulas;
    ArrayList<ReservasHuesped> datos;
    ReservasHuespedAdapter adaptador;
    String idReservaSeleccionada = null;
    ReservasHuesped reservaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        txtNombre = findViewById(R.id.txtNombre);
        txtApellido = findViewById(R.id.txtApellido);
        spinnerCedulas = findViewById(R.id.spinnerCedulas);
        spinnerHabitaciones = findViewById(R.id.spinnerHabitaciones);
        txtInicio = findViewById(R.id.txtInicio);
        txtFin = findViewById(R.id.txtFin);
        txtCantidad = findViewById(R.id.txtCantidad);
        btnReservar = findViewById(R.id.btnReservar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        lista = findViewById(R.id.lista);
        btnRegresar = findViewById(R.id.btnRegresar);

        db = new DBGestion(this, "BaseDatos", null, 2);
        sql = db.getWritableDatabase();

        cargarCedulas();
        cargarHabitaciones();
        cargarReservas();
        eventoSpinnerCedulas();

        btnReservar.setOnClickListener(v -> crearReserva());
        btnEliminar.setOnClickListener(v -> {
            if (idReservaSeleccionada != null) {
                eliminarReserva(idReservaSeleccionada);
                idReservaSeleccionada = null;
            } else {
                Toast.makeText(this, "Seleccione una reserva", Toast.LENGTH_SHORT).show();
            }
        });
        lista.setOnItemClickListener((parent, view, position, id) -> {
            ReservasHuesped reserva = datos.get(position);
            idReservaSeleccionada = reserva.getIdReserva();
            reservaSeleccionada = reserva;
            Toast.makeText(this, "Seleccionó reserva: " + idReservaSeleccionada, Toast.LENGTH_SHORT).show();
        });
        btnEditar.setOnClickListener(v -> {
            if (reservaSeleccionada == null) {
                Toast.makeText(this, "Seleccione una reserva", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, EditarReservas.class);
            intent.putExtra("id_reserva", reservaSeleccionada.getIdReserva());
            intent.putExtra("cedula", reservaSeleccionada.getCedulaHuesped());
            intent.putExtra("habitacion", reservaSeleccionada.getHabitacion());
            intent.putExtra("inicio", reservaSeleccionada.getFechaInicio());
            intent.putExtra("fin", reservaSeleccionada.getFechaFin());
            startActivityForResult(intent, 1001);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarReservas();
    }

    public void Volver(View view)  {
        Intent intent = new Intent(this, Huesped.class);
        startActivity(intent);
    }

    private void eventoSpinnerCedulas() {

        spinnerCedulas.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {

                String cedulaSeleccionada = listaCedulas.get(position);

                Cursor c = sql.rawQuery(
                        "SELECT nombre, apellido FROM huesped WHERE cedula = ?",
                        new String[]{cedulaSeleccionada}
                );

                if (c.moveToFirst()) {
                    txtNombre.setText(c.getString(0));
                    txtApellido.setText(c.getString(1));
                }

                c.close();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }

    private String generarIdReserva() {
        Cursor c = sql.rawQuery("SELECT id_reserva FROM reserva ORDER BY id_reserva DESC LIMIT 1", null);
        String nuevoId = "R0001";
        if (c.moveToFirst()) {
            String ultimoId = c.getString(0);
            int numero = Integer.parseInt(ultimoId.substring(1));
            numero++;
            nuevoId = String.format("R%04d", numero);
        }
        c.close();
        return nuevoId;
    }

    private void crearReserva() {

        String cedulaSeleccionada = spinnerCedulas.getSelectedItem().toString();
        String idReserva = generarIdReserva();
        ContentValues reserva = new ContentValues();
        reserva.put("id_reserva", idReserva);
        reserva.put("cedula_empleado", "EMP001");
        reserva.put("codigo_habitacion", spinnerHabitaciones.getSelectedItem().toString());
        reserva.put("fecha_inicio", txtInicio.getText().toString());
        reserva.put("fecha_fin", txtFin.getText().toString());
        reserva.put("Total", "0");
        sql.insert("reserva", null, reserva);
        int cantidad = Integer.parseInt(txtCantidad.getText().toString());

        for (int i = 0; i < cantidad; i++) {
            ContentValues rh = new ContentValues();
            rh.put("idreserva", idReserva);
            rh.put("cedula_huesped", cedulaSeleccionada);
            rh.put("estado", "Pendiente");
            sql.insert("reserva_huesped", null, rh);
        }

        Toast.makeText(this, "Reserva creada correctamente", Toast.LENGTH_LONG).show();
        cargarReservas();
    }

    private void eliminarReserva(String idReserva) {

        sql.delete("reserva_huesped", "idreserva=?", new String[]{idReserva});
        int filas = sql.delete("reserva", "id_reserva=?", new String[]{idReserva});

        if (filas > 0) {
            Toast.makeText(this, "Reserva eliminada", Toast.LENGTH_SHORT).show();
            cargarReservas();
        } else {
            Toast.makeText(this, "No existe la reserva", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarReservas() {

        datos = new ArrayList<>();

        Cursor c = sql.rawQuery(
                "SELECT r.id_reserva, r.codigo_habitacion, r.fecha_inicio, r.fecha_fin, " +
                        "h.cedula, h.nombre, rh.estado " +
                        "FROM reserva r " +
                        "INNER JOIN reserva_huesped rh ON r.id_reserva = rh.idreserva " +
                        "INNER JOIN huesped h ON h.cedula = rh.cedula_huesped",
                null
        );

        if (c.moveToFirst()) {
            do {
                ReservasHuesped g = new ReservasHuesped();

                g.setIdReserva(c.getString(0));
                g.setHabitacion(c.getString(1));
                g.setFechaInicio(c.getString(2));
                g.setFechaFin(c.getString(3));
                g.setCedulaHuesped(c.getString(4));
                g.setNombreHuesped(c.getString(5));
                g.setEstado(c.getString(6));
                g.setImagen(R.drawable.habitacion);

                datos.add(g);

            } while (c.moveToNext());
        }

        c.close();

        adaptador = new ReservasHuespedAdapter(this, datos);
        lista.setAdapter(adaptador);
    }

    private void cargarHabitaciones() {
        listaHabitaciones = new ArrayList<>();
        Cursor c = sql.rawQuery("SELECT codigo FROM habitacion", null);

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

    private void cargarCedulas() {

        listaCedulas = new ArrayList<>();
        Cursor c = sql.rawQuery("SELECT cedula FROM huesped", null);

        if (c.moveToFirst()) {
            do {
                listaCedulas.add(c.getString(0));
            } while (c.moveToNext());
        }
        c.close();
        adapterCedulas = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                listaCedulas
        );

        spinnerCedulas.setAdapter(adapterCedulas);
    }

}
