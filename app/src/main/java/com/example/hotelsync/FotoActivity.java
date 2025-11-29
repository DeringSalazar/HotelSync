package com.example.hotelsync;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;

import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;

public class FotoActivity extends AppCompatActivity {

    ImageView imgFoto;
    EditText txtCodigo, txtNombre, txtEmpleado;
    Button btnTomarFoto, btnGuardar, btnCancelar;
    Bitmap imagenSeleccionada = null;
    ActivityResultLauncher<Intent> lanzadorTomarFoto;
    SQLiteDatabase sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtEmpleado = findViewById(R.id.txtEmpleado);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        DBGestion helper = new DBGestion(this, "BaseDatos", null, 2);
        sql = helper.getWritableDatabase();

        btnTomarFoto.setOnClickListener(v -> tomarFoto());
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


}
