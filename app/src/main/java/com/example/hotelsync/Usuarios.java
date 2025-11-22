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

public class Usuarios extends AppCompatActivity {

    RadioGroup radioGroupRol;
    RadioButton rbHuesped, rbEmpleado;
    EditText edtCedula, edtNombre, edtApellido, edtTelefono, edtCorreo;
    Button btnInsertar, btnBuscar, btnActualizar, btnEliminar, btnRegresar;
    ListView listViewUsuarios;

    DBGestion admin;
    SQLiteDatabase basedatos;

    ArrayList<Vista> listaUsuarios;
    UsuariosAdapter adaptador;

    String cedulaSeleccionada  = null;
    Vista seleccionadoGlobal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        radioGroupRol = findViewById(R.id.radioGroupRol);
        rbHuesped = findViewById(R.id.rbHuesped);
        rbEmpleado = findViewById(R.id.rbEmpleado);
        edtCedula = findViewById(R.id.edtCedula);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtCorreo = findViewById(R.id.edtCorreo);
        btnInsertar = findViewById(R.id.btnInsertar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnRegresar = findViewById(R.id.btnRegresar);
        listViewUsuarios = findViewById(R.id.listViewUsuarios);

        String tipo = getIntent().getStringExtra("tipo");
        if (tipo != null) {
            if (tipo.equals("huesped")) {
                rbEmpleado.setEnabled(false);
                rbHuesped.setChecked(true);
                rbEmpleado.setVisibility(View.INVISIBLE);
            } else if (tipo.equals("empleado")) {
                rbHuesped.setEnabled(false);
                rbEmpleado.setChecked(true);
                rbHuesped.setVisibility(View.INVISIBLE);
            }
        }

        admin = new DBGestion(this, "BaseDatos", null, 2);
        basedatos = admin.getWritableDatabase();

        listaUsuarios = new ArrayList<>();
        adaptador = new UsuariosAdapter(this, listaUsuarios);
        listViewUsuarios.setAdapter(adaptador);

        btnInsertar.setOnClickListener(v -> insertarRegistro());
        btnBuscar.setOnClickListener(v -> buscarRegistro());
        btnEliminar.setOnClickListener(v -> eliminarRegistro());


        btnEliminar.setEnabled(false);
        btnActualizar.setEnabled(false);
        radioGroupRol.setOnCheckedChangeListener((group, checkedId) -> cargarListaPorRol());

        listViewUsuarios.setOnItemClickListener((parent, view, position, id) -> {
            Vista seleccionado = (Vista) parent.getItemAtPosition(position);
            seleccionadoGlobal = seleccionado;

            cedulaSeleccionada = String.valueOf(seleccionado.getCedula());

            edtCedula.setText(String.valueOf(seleccionado.getCedula()));
            edtNombre.setText(seleccionado.getNombre());
            edtApellido.setText(seleccionado.getApellido());
            edtTelefono.setText(String.valueOf(seleccionado.getTelefono()));
            edtCorreo.setText(seleccionado.getCorreo());

            btnEliminar.setEnabled(true);
            btnActualizar.setEnabled(true);
        });

        btnActualizar.setOnClickListener(v -> {
            if (seleccionadoGlobal == null) {
                Toast.makeText(Usuarios.this, "Seleccione un registro para editar", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(Usuarios.this, EditarUsuario.class);
            i.putExtra("cedula", String.valueOf(seleccionadoGlobal.getCedula()));
            i.putExtra("nombre", seleccionadoGlobal.getNombre());
            i.putExtra("apellido", seleccionadoGlobal.getApellido());
            i.putExtra("telefono", String.valueOf(seleccionadoGlobal.getTelefono()));
            i.putExtra("correo", seleccionadoGlobal.getCorreo());
            i.putExtra("rol", rolSeleccionado());
            startActivity(i);
        });

        cargarListaPorRol();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaPorRol();
    }

    public void Volver(View view)  {
        Intent intent = new Intent(this, Huesped.class);
        startActivity(intent);
    }

    private String rolSeleccionado() {
        return rbHuesped.isChecked() ? "huesped" : "empleado";
    }

    private void insertarRegistro() {
        String tabla = rolSeleccionado();
        String cedula = edtCedula.getText().toString().trim();
        String nombre = edtNombre.getText().toString().trim();
        String apellido = edtApellido.getText().toString().trim();
        String telefono = edtTelefono.getText().toString().trim();
        String correo = edtCorreo.getText().toString().trim();

        if (cedula.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || correo.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("cedula", cedula);
        cv.put("nombre", nombre);
        cv.put("apellido", apellido);
        cv.put("telefono", telefono);
        cv.put("correo", correo);

        try {
            long res = basedatos.insertOrThrow(tabla, null, cv);
            if (res != -1) {
                Toast.makeText(this, "Insertado en " + tabla, Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarListaPorRol();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al insertar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void buscarRegistro() {
        String tabla = rolSeleccionado();
        String cedula = edtCedula.getText().toString().trim();
        if (cedula.isEmpty()) {
            Toast.makeText(this, "Ingrese cédula para buscar", Toast.LENGTH_SHORT).show();

            return;
        }

        Cursor c = basedatos.rawQuery(
                "SELECT cedula, nombre, apellido, telefono, correo FROM " + tabla + " WHERE cedula=?",
                new String[]{cedula});

        if (c.moveToFirst()) {
            edtCedula.setText(c.getString(0));
            edtNombre.setText(c.getString(1));
            edtApellido.setText(c.getString(2));
            edtTelefono.setText(c.getString(3));
            edtCorreo.setText(c.getString(4));
            Toast.makeText(this, "Registro encontrado en " + tabla, Toast.LENGTH_SHORT).show();
            btnEliminar.setEnabled(true);
            btnActualizar.setEnabled(true);
        } else {
            Toast.makeText(this, "No existe la cédula en " + tabla, Toast.LENGTH_SHORT).show();
        }
        c.close();
    }

    private void eliminarRegistro() {
        if (cedulaSeleccionada == null) {
            Toast.makeText(this, "Seleccione un registro", Toast.LENGTH_SHORT).show();
            return;
        }

        String tabla = rolSeleccionado();

        int filas = basedatos.delete(tabla, "cedula=?", new String[]{cedulaSeleccionada});

        if (filas > 0) {
            Toast.makeText(this, "Eliminado de " + tabla, Toast.LENGTH_SHORT).show();
            limpiarCampos();
            cargarListaPorRol();
            cedulaSeleccionada = null;
            btnEliminar.setEnabled(false);
            btnActualizar.setEnabled(false);
        } else {
            Toast.makeText(this, "No se encontró la cédula en " + tabla, Toast.LENGTH_SHORT).show();
        }
    }


    private void cargarListaPorRol() {
        String tabla = rolSeleccionado();
        listaUsuarios.clear();

        Cursor c = basedatos.rawQuery(
                "SELECT cedula, nombre, apellido, telefono, correo FROM " + tabla,
                null);

        if (c.moveToFirst()) {
            do {

                Vista v = new Vista(
                        c.getInt(0),
                        R.drawable.user_h,
                        c.getString(1),
                        c.getString(2),
                        c.getInt(3),
                        c.getString(4)
                );

                listaUsuarios.add(v);

            } while (c.moveToNext());
        }
        c.close();

        adaptador.notifyDataSetChanged();
    }

    private void limpiarCampos() {
        edtCedula.setText("");
        edtNombre.setText("");
        edtApellido.setText("");
        edtTelefono.setText("");
        edtCorreo.setText("");
    }
}