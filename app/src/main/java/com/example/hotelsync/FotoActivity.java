package com.example.hotelsync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FotoActivity extends AppCompatActivity {

    Spinner spinnerHabitaciones;
    ImageView imgFoto;
    Button btnTomarFoto, btnGuardar, btnActualizar, btnEliminar;
    GridView listaFotos;
    ArrayList<ItemFoto> fotos;
    FotoAdapter adapter;
    Bitmap imagenSeleccionada = null;
    ArrayList<String> listaHabitaciones;
    ArrayAdapter<String> adapterHabitaciones;
    ActivityResultLauncher<Intent> lanzadorTomarFoto;
    SQLiteDatabase sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        spinnerHabitaciones = findViewById(R.id.spinnerHabitaciones);
        imgFoto = findViewById(R.id.imgFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        listaFotos = findViewById(R.id.listaFotos);

        DBGestion helper = new DBGestion(this, "BaseDatos", null, 2);
        sql = helper.getWritableDatabase();

        lanzadorTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        Bundle extras = resultado.getData().getExtras();
                        imagenSeleccionada = (Bitmap) extras.get("data");
                        imgFoto.setImageBitmap(imagenSeleccionada);
                    }
                }
        );

        btnTomarFoto.setOnClickListener(v -> tomarFoto());
        btnGuardar.setOnClickListener(v -> guardarFoto());
        btnActualizar.setOnClickListener(v -> actualizarFoto());
        btnEliminar.setOnClickListener(v -> eliminarFoto());

        cargarHabitaciones();

        // actualizar galería cuando cambia la selección
        spinnerHabitaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarGaleria();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        lanzadorTomarFoto.launch(intent);
    }

    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        return stream.toByteArray();
    }

    private void guardarFoto() {

        if (imagenSeleccionada == null) {
            Toast.makeText(this, "Primero tome una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        String codigo = spinnerHabitaciones.getSelectedItem().toString().trim();

        ContentValues values = new ContentValues();
        values.put("codigo_habit", codigo);
        values.put("foto", bitmapToBytes(imagenSeleccionada));

        long resultado = sql.insert("multimedia_habitacion", null, values);

        if (resultado != -1) {
            Toast.makeText(this, "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
            cargarGaleria();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_LONG).show();
        }
    }


    private void cargarGaleria() {
        String codigo = spinnerHabitaciones.getSelectedItem().toString();
        fotos = cargarFotos(codigo);

        adapter = new FotoAdapter(this, fotos);
        listaFotos.setAdapter(adapter);
    }

    private ArrayList<ItemFoto> cargarFotos(String codigoHabitacion) {
        ArrayList<ItemFoto> lista = new ArrayList<>();

        Cursor cursor = sql.rawQuery(
                "SELECT id, foto FROM multimedia_habitacion WHERE codigo_habit = ?",
                new String[]{codigoHabitacion}
        );

        if (cursor.moveToFirst()) {
            do {
                lista.add(new ItemFoto(
                        cursor.getInt(0),
                        cursor.getBlob(1)
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return lista;
    }

    private void actualizarFoto() {
        String codigo = spinnerHabitaciones.getSelectedItem().toString();

        if (imagenSeleccionada == null) {
            Toast.makeText(this, "Primero tome una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("foto", bitmapToBytes(imagenSeleccionada));

        int filas = sql.update("multimedia_habitacion", values,
                "codigo_habit=?", new String[]{codigo});

        if (filas > 0) {
            Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show();
            cargarGaleria();
        } else {
            Toast.makeText(this, "No existe ese código", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarFoto() {
        String codigo = spinnerHabitaciones.getSelectedItem().toString();

        int filas = sql.delete("multimedia_habitacion",
                "codigo_habit=?", new String[]{codigo});

        if (filas > 0) {
            Toast.makeText(this, "Foto(s) eliminada(s)", Toast.LENGTH_SHORT).show();
            cargarGaleria();
        } else {
            Toast.makeText(this, "No existe ese código", Toast.LENGTH_SHORT).show();
        }
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
}
