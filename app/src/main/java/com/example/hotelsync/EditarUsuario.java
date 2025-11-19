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

public class EditarUsuario extends AppCompatActivity {

    EditText edtCedulaEdit, edtNombreEdit, edtApellidoEdit, edtTelefonoEdit, edtCorreoEdit;
    Button btnGuardar, btnCancelar;
    ListView listViewEditUsuarios;

    ArrayList<Vista> listaUsuarios;
    UsuariosAdapter adaptador;

    DBGestion admin;
    SQLiteDatabase basedatos;
    String cedulaOriginal;
    String rol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_usuario);

        // ----- INICIALIZAR COMPONENTES -----
        edtCedulaEdit = findViewById(R.id.edtCedulaEdit);
        edtNombreEdit = findViewById(R.id.edtNombreEdit);
        edtApellidoEdit = findViewById(R.id.edtApellidoEdit);
        edtTelefonoEdit = findViewById(R.id.edtTelefonoEdit);
        edtCorreoEdit = findViewById(R.id.edtCorreoEdit);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        listViewEditUsuarios = findViewById(R.id.listViewEditUsuarios);

        listaUsuarios = new ArrayList<>();
        adaptador = new UsuariosAdapter(this, listaUsuarios);
        listViewEditUsuarios.setAdapter(adaptador);

        admin = new DBGestion(this, "BaseDatos", null, 1);
        basedatos = admin.getWritableDatabase();

        cedulaOriginal = getIntent().getStringExtra("cedula");
        rol = getIntent().getStringExtra("rol");
        String nombre = getIntent().getStringExtra("nombre");
        String apellido = getIntent().getStringExtra("apellido");
        String telefono = getIntent().getStringExtra("telefono");
        String correo = getIntent().getStringExtra("correo");

        edtCedulaEdit.setText(cedulaOriginal);
        edtCedulaEdit.setEnabled(false);
        edtNombreEdit.setText(nombre);
        edtApellidoEdit.setText(apellido);
        edtTelefonoEdit.setText(telefono);
        edtCorreoEdit.setText(correo);

        cargarLista();
        btnGuardar.setOnClickListener(v -> {
            String nombreN = edtNombreEdit.getText().toString().trim();
            String apellidoN = edtApellidoEdit.getText().toString().trim();
            String telefonoN = edtTelefonoEdit.getText().toString().trim();
            String correoN = edtCorreoEdit.getText().toString().trim();

            if (nombreN.isEmpty() || apellidoN.isEmpty() ||
                    telefonoN.isEmpty() || correoN.isEmpty()) {

                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            ContentValues cv = new ContentValues();
            cv.put("nombre", nombreN);
            cv.put("apellido", apellidoN);
            cv.put("telefono", telefonoN);
            cv.put("correo", correoN);

            String tabla = rol.equals("empleado") ? "empleado" : "huesped";

            int filas = basedatos.update(tabla, cv, "cedula=?", new String[]{cedulaOriginal});

            if (filas > 0) {
                Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();
                cargarLista();
            } else {
                Toast.makeText(this, "No se pudo actualizar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Volver(View view)  {
        Intent intent = new Intent(this, Usuarios.class);
        intent.putExtra("tipo", rol);
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    private void cargarLista() {
        listaUsuarios.clear();

        String tabla = rol.equals("empleado") ? "empleado" : "huesped";

        Cursor c = basedatos.rawQuery(
                "SELECT cedula, nombre, apellido, telefono, correo FROM " + tabla,
                null
        );

        if (c.moveToFirst()) {
            do {
                int ced = c.getInt(0);
                String nombre = c.getString(1);
                String apellido = c.getString(2);
                int telefono = c.getInt(3);
                String correo = c.getString(4);

                int imagen = R.drawable.user_h;
                Vista item = new Vista(ced, imagen, nombre, apellido, telefono, correo);
                listaUsuarios.add(item);
            } while (c.moveToNext());
        }

        c.close();
        adaptador.notifyDataSetChanged();
    }
}
