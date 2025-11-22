package com.example.hotelsync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    EditText txtFiltroCodigo;
    Button btnTomarFoto, btnGuardar, btnBuscarFoto, btnActualizar, btnEliminar;
    GridView listaFotos;
    ArrayList<ItemFoto> fotos;
    FotoAdapter adapter;
    Bitmap imagenSeleccionada = null;
    ArrayList<String> listaHabitaciones;
    ArrayAdapter<String> adapterHabitaciones;
    ActivityResultLauncher<Intent> lanzadorTomarFoto;
    SQLiteDatabase sql;
    int idFotoSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        txtFiltroCodigo = findViewById(R.id.txtFiltroCodigo);
        spinnerHabitaciones = findViewById(R.id.spinnerHabitaciones);
        imgFoto = findViewById(R.id.imgFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnBuscarFoto = findViewById(R.id.btnBuscarFoto);
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
        btnBuscarFoto.setOnClickListener(v -> buscarPorCodigo());
        btnActualizar.setOnClickListener(v -> actualizarFoto());
        btnEliminar.setOnClickListener(v -> eliminarFoto());

        cargarHabitaciones();

        listaFotos.setOnItemClickListener((parent, view, position, id) -> {
            ItemFoto foto = fotos.get(position);
            idFotoSeleccionada = foto.getId();
            Bitmap bitmap = BitmapFactory.decodeByteArray(foto.getFoto(), 0, foto.getFoto().length);
            imgFoto.setImageBitmap(bitmap);
            imagenSeleccionada = bitmap;
            Toast.makeText(this, "Foto seleccionada para edición", Toast.LENGTH_SHORT).show();
        });

        spinnerHabitaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cargarGaleria();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public void Volver(View view) {
        Intent intent = new Intent(this, HabitacionActivity.class);
        startActivity(intent);
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
        Cursor cursor = sql.rawQuery(
                "SELECT id FROM multimedia_habitacion WHERE codigo_habit = ?",
                new String[]{codigo}
        );
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Ya existe una foto para este código. Cambie el código o actualice la imagen.", Toast.LENGTH_LONG).show();
            cursor.close();
            return;
        }

        cursor.close();

        ContentValues values = new ContentValues();
        values.put("codigo_habit", codigo);
        values.put("foto", bitmapToBytes(imagenSeleccionada));

        long resultado = sql.insert("multimedia_habitacion", null, values);

        if (resultado != -1) {
            Toast.makeText(this, "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
            cargarGaleria();
        } else {
            Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_LONG).show();
        }
    }


    private void buscarPorCodigo() {
        String codigo = txtFiltroCodigo.getText().toString().trim();

        if (codigo.isEmpty()) {
            Toast.makeText(this, "Ingrese un código", Toast.LENGTH_SHORT).show();
            return;
        }

        fotos = new ArrayList<>();

        Cursor cursor = sql.rawQuery(
                "SELECT id, foto FROM multimedia_habitacion WHERE codigo_habit = ?",
                new String[]{codigo}
        );

        if (cursor.moveToFirst()) {
            int idPrimero = cursor.getInt(0);
            byte[] fotoBytesPrimera = cursor.getBlob(1);
            Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytesPrimera, 0, fotoBytesPrimera.length);

            imgFoto.setImageBitmap(bitmap);
            imagenSeleccionada = bitmap;
            idFotoSeleccionada = idPrimero;

            do {
                fotos.add(new ItemFoto(
                        cursor.getInt(0),
                        cursor.getBlob(1)
                ));
            } while (cursor.moveToNext());

        } else {
            Toast.makeText(this, "No se encontraron fotos con ese código", Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        adapter = new FotoAdapter(this, fotos);
        listaFotos.setAdapter(adapter);
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
        if (idFotoSeleccionada == -1) {
            Toast.makeText(this, "Seleccione una foto primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenSeleccionada == null) {
            Toast.makeText(this, "Tome o seleccione una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("foto", bitmapToBytes(imagenSeleccionada));

        int filas = sql.update("multimedia_habitacion", values,
                "id=?", new String[]{String.valueOf(idFotoSeleccionada)});

        if (filas > 0) {
            Toast.makeText(this, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show();
            cargarGaleria();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }


    private void eliminarFoto() {
        if (idFotoSeleccionada == -1) {
            Toast.makeText(this, "Seleccione una foto primero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imagenSeleccionada == null) {
            Toast.makeText(this, "Tome o seleccione una foto", Toast.LENGTH_SHORT).show();
            return;
        }

        int filas = sql.delete("multimedia_habitacion",
                "id=?", new String[]{String.valueOf(idFotoSeleccionada)});

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
