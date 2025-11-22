package com.example.hotelsync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditarReservas extends AppCompatActivity {

    EditText txtInicioEditar, txtFinEditar, txtCantidadEditar;
    Spinner spinnerCedulasEdit, spinnerHabitacionesEdit;
    Button btnGuardarCambios;
    ListView listViewReservasEditar;

    DBGestion db;
    SQLiteDatabase sql;

    String idReserva;
    ArrayList<String> listaHabitaciones;
    ArrayList<String> listaCedulas;
    ArrayAdapter<String> adapterHabitaciones;
    ArrayAdapter<String> adapterCedulas;

    ArrayList<ReservasHuesped> datos;
    ReservasHuespedAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_reservas);

        txtInicioEditar = findViewById(R.id.txtInicioEditar);
        txtFinEditar = findViewById(R.id.txtFinEditar);
        txtCantidadEditar = findViewById(R.id.txtCantidadEditar);
        spinnerCedulasEdit = findViewById(R.id.spinnerCedulasEdit);
        spinnerHabitacionesEdit = findViewById(R.id.spinnerHabitacionesEdit);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        listViewReservasEditar = findViewById(R.id.listViewReservasEditar);

        db = new DBGestion(this, "BaseDatos", null, 2);
        sql = db.getWritableDatabase();

        idReserva = getIntent().getStringExtra("id_reserva");
        String cedula = getIntent().getStringExtra("cedula");
        String habitacion = getIntent().getStringExtra("habitacion");
        String inicio = getIntent().getStringExtra("inicio");
        String fin = getIntent().getStringExtra("fin");
        String estado = getIntent().getStringExtra("estado");

        cargarHabitaciones();
        cargarCedulas();

        txtInicioEditar.setText(inicio);
        txtFinEditar.setText(fin);

        if (habitacion != null) {
            int posHab = listaHabitaciones.indexOf(habitacion);
            if (posHab >= 0) spinnerHabitacionesEdit.setSelection(posHab);
        }
        if (cedula != null) {
            int posCed = listaCedulas.indexOf(cedula);
            if (posCed >= 0) spinnerCedulasEdit.setSelection(posCed);
        }

        datos = new ArrayList<>();
        adaptador = new ReservasHuespedAdapter(this, datos);
        listViewReservasEditar.setAdapter(adaptador);

        cargarListaReserva(idReserva);
        listViewReservasEditar.setOnItemClickListener((parent, view, position, id) -> {
            ReservasHuesped r = datos.get(position);
            int posCed = listaCedulas.indexOf(r.getCedulaHuesped());
            if (posCed >= 0) spinnerCedulasEdit.setSelection(posCed);

            int posHab = listaHabitaciones.indexOf(r.getHabitacion());
            if (posHab >= 0) spinnerHabitacionesEdit.setSelection(posHab);

            txtInicioEditar.setText(r.getFechaInicio());
            txtFinEditar.setText(r.getFechaFin());
            txtCantidadEditar.setText("1");
        });

        btnGuardarCambios.setOnClickListener(v -> {
            guardarCambios();
        });
    }

    public void Volver(View view)  {
        Intent intent = new Intent(this, ReservaActivity.class);
        setResult(RESULT_OK, intent);
        startActivity(intent);
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
        adapterHabitaciones = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaHabitaciones);
        spinnerHabitacionesEdit.setAdapter(adapterHabitaciones);
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
        adapterCedulas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listaCedulas);
        spinnerCedulasEdit.setAdapter(adapterCedulas);
    }

    private void cargarListaReserva(String idReserva) {
        datos.clear();

        String query = "SELECT r.id_reserva, r.codigo_habitacion, r.fecha_inicio, r.fecha_fin, " +
                "h.cedula, h.nombre, rh.estado " +
                "FROM reserva r " +
                "INNER JOIN reserva_huesped rh ON r.id_reserva = rh.idreserva " +
                "INNER JOIN huesped h ON h.cedula = rh.cedula_huesped " +
                "WHERE r.id_reserva = ?";

        Cursor c = sql.rawQuery(query, new String[]{ idReserva });

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
        adaptador.notifyDataSetChanged();
    }

    private void guardarCambios() {
        String cedulaSeleccionada = spinnerCedulasEdit.getSelectedItem().toString();
        String habitacionSeleccionada = spinnerHabitacionesEdit.getSelectedItem().toString();
        String inicio = txtInicioEditar.getText().toString().trim();
        String fin = txtFinEditar.getText().toString().trim();

        if (inicio.isEmpty() || fin.isEmpty()) {
            Toast.makeText(this, "Complete fechas", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("codigo_habitacion", habitacionSeleccionada);
        cv.put("fecha_inicio", inicio);
        cv.put("fecha_fin", fin);
        cv.put("Total", "0");

        int filasReserva = sql.update("reserva", cv, "id_reserva=?", new String[]{ idReserva });

        ContentValues ch = new ContentValues();
        ch.put("cedula_huesped", cedulaSeleccionada);

        int filasRh = sql.update("reserva_huesped", ch, "idreserva=?", new String[]{ idReserva });

        if (filasReserva > 0 || filasRh > 0) {
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            cargarListaReserva(idReserva);
        } else {
            Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
        }
    }
}
