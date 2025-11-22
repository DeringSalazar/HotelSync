package com.example.hotelsync;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class AudioActivity extends AppCompatActivity {

    EditText edtCodigo;
    TextView txtEstadoAudio;
    Button btnGrabar, btnDetener, btnReproducir, btnGuardar, btnBuscar, btnActualizar, btnEliminar;

    MediaRecorder grabador;
    MediaPlayer reproductor;

    String rutaAudio = "";
    SQLiteDatabase db;

    private static final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        edtCodigo = findViewById(R.id.edtCodigo);
        txtEstadoAudio = findViewById(R.id.TxtEstadoAudio);

        btnGrabar = findViewById(R.id.btnGrabar);
        btnDetener = findViewById(R.id.btnDetener);
        btnReproducir = findViewById(R.id.btnReproducir);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);

        DBGestion helper = new DBGestion(this, "BaseDatos", null, 2);
        db = helper.getWritableDatabase();

        pedirPermisos();

        btnGrabar.setOnClickListener(v -> iniciarGrabacion());
        btnDetener.setOnClickListener(v -> detenerGrabacion());
        btnReproducir.setOnClickListener(v -> reproducirAudio());
        btnGuardar.setOnClickListener(v -> guardarAudio());
        btnBuscar.setOnClickListener(v -> buscarAudio());
        btnActualizar.setOnClickListener(v -> actualizarAudio());
        btnEliminar.setOnClickListener(v -> eliminarAudio());
    }

    private void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_CODE
            );
        }
    }

    private void iniciarGrabacion() {
        rutaAudio = getExternalCacheDir().getAbsolutePath() + "/audio_temp.3gp";

        grabador = new MediaRecorder();
        grabador.setAudioSource(MediaRecorder.AudioSource.MIC);
        grabador.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        grabador.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        grabador.setOutputFile(rutaAudio);

        try {
            grabador.prepare();
            grabador.start();

            btnGrabar.setEnabled(false);
            btnDetener.setEnabled(true);

            Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerGrabacion() {
        try {
            grabador.stop();
            grabador.release();
            grabador = null;

            btnDetener.setEnabled(false);
            btnGrabar.setEnabled(true);
            btnReproducir.setEnabled(true);

            txtEstadoAudio.setText("Audio grabado ✔");

        } catch (Exception e) {
            Toast.makeText(this, "Error al detener", Toast.LENGTH_SHORT).show();
        }
    }

    private void reproducirAudio() {
        try {
            reproductor = new MediaPlayer();
            reproductor.setDataSource(rutaAudio);
            reproductor.prepare();
            reproductor.start();
        } catch (Exception e) {
            Toast.makeText(this, "No se puede reproducir", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarAudio() {
        String codigo = edtCodigo.getText().toString().trim();

        if (codigo.isEmpty() || rutaAudio.isEmpty()) {
            Toast.makeText(this, "Ingrese código y grabe audio", Toast.LENGTH_SHORT).show();
            return;
        }

        byte[] audioBytes = convertirAudioBytes();
        if (audioBytes == null) {
            Toast.makeText(this, "Error al convertir audio", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues valores = new ContentValues();
        valores.put("codigo_habitacion", codigo);
        valores.put("audio", audioBytes);

        db.insert("multimedia_habitacion", null, valores);

        txtEstadoAudio.setText("Audio guardado ✔");
        Toast.makeText(this, "Audio guardado", Toast.LENGTH_SHORT).show();
    }

    private byte[] convertirAudioBytes() {
        try {
            File file = new File(rutaAudio);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int read;

            while ((read = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }

            return bos.toByteArray();

        } catch (Exception e) {
            return null;
        }
    }

    private void buscarAudio() {
        String codigo = edtCodigo.getText().toString().trim();

        Cursor c = db.rawQuery(
                "SELECT audio FROM multimedia_habitacion WHERE codigo_habitacion = ?",
                new String[]{codigo}
        );

        if (c.moveToFirst()) {
            try {
                byte[] audioBytes = c.getBlob(0);

                File file = new File(getExternalCacheDir(), "audio_recuperado.3gp");
                rutaAudio = file.getAbsolutePath();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(audioBytes);
                fos.close();

                txtEstadoAudio.setText("Audio cargado ✔");
                btnActualizar.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnReproducir.setEnabled(true);

            } catch (Exception e) {
                Toast.makeText(this, "Error al recuperar audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            txtEstadoAudio.setText("No hay audio");
            Toast.makeText(this, "No se encontró audio", Toast.LENGTH_SHORT).show();
        }

        c.close();
    }

    private void actualizarAudio() {
        String codigo = edtCodigo.getText().toString().trim();
        byte[] audioBytes = convertirAudioBytes();

        ContentValues valores = new ContentValues();
        valores.put("audio", audioBytes);

        db.update("multimedia_habitacion",
                valores,
                "codigo_habitacion=?",
                new String[]{codigo});

        txtEstadoAudio.setText("Audio actualizado ✔");
        Toast.makeText(this, "Audio actualizado", Toast.LENGTH_SHORT).show();
    }

    private void eliminarAudio() {
        String codigo = edtCodigo.getText().toString().trim();

        db.delete("multimedia_habitacion",
                "codigo_habitacion=?",
                new String[]{codigo});

        txtEstadoAudio.setText("Audio eliminado");
        rutaAudio = "";
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnReproducir.setEnabled(false);

        Toast.makeText(this, "Audio eliminado", Toast.LENGTH_SHORT).show();
    }
}
