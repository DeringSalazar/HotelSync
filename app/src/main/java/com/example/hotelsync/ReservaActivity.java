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

public class ReservaActivity extends AppCompatActivity {

    EditText txtNombre, txtApellido, txtInicio, txtFin, txtCantidad;
    Button btnReservar, btnEditar, btnEliminar, btnRegresar;
    ListView lista;

    DBGestion db;
    SQLiteDatabase sql;

    Spinner spinnerHabitaciones, spinnerCedulas;
    ArrayList<String> listaHabitaciones, listaCedulas, datos;
    ArrayAdapter<String> adapterHabitaciones, adapterCedulas, adaptador;

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

        db = new DBGestion(this, "BaseDatos", null, 1);
        sql = db.getWritableDatabase();

        cargarCedulas();
        cargarHabitaciones();
        cargarReservas();
        eventoSpinnerCedulas();

        btnReservar.setOnClickListener(v -> crearReserva());
        btnEditar.setOnClickListener(v -> editarReserva());
        btnEliminar.setOnClickListener(v -> eliminarReserva());
        btnRegresar.setOnClickListener(v -> finish());
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
                    txtNombre.setText(c.getString(0));   // nombre
                    txtApellido.setText(c.getString(1)); // apellido
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

    private void editarReserva() {

        String codigo = spinnerCedulas.getSelectedItem().toString();
        String cedulaSeleccionada = spinnerCedulas.getSelectedItem().toString();

        ContentValues r = new ContentValues();
        r.put("cedula_empleado", "EMP001");
        r.put("codigo_habitacion", spinnerHabitaciones.getSelectedItem().toString());
        r.put("fecha_inicio", txtInicio.getText().toString());
        r.put("fecha_fin", txtFin.getText().toString());
        r.put("Total", "0");

        int fila1 = sql.update("reserva", r, "id_reserva=?", new String[]{codigo});

        ContentValues h = new ContentValues();
        h.put("cedula_huesped", cedulaSeleccionada);

        int fila2 = sql.update("reserva_huesped", h, "idreserva=?", new String[]{codigo});

        if (fila1 > 0 && fila2 > 0) {
            Toast.makeText(this, "Reserva actualizada", Toast.LENGTH_SHORT).show();
            cargarReservas();
        } else {
            Toast.makeText(this, "No existe la reserva", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarReserva() {

        String codigo = spinnerCedulas.getSelectedItem().toString();

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

                datos.add(
                        "Reserva: " + id +
                                "\nHabitación: " + habitacion +
                                "\nInicio: " + inicio +
                                "\nFin: " + fin
                );
            } while (c.moveToNext());
        }

        c.close();

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
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
