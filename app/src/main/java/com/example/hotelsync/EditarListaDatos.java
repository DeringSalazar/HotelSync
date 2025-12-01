package com.example.hotelsync;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditarListaDatos extends AppCompatActivity {


    EditText txtCodigoEdit, txtNombreEdit, txtCedulaEdit, txtLatEdit, txtLonEdit;
    Button btnActualizar, btnEliminar, btnReproducirAudio, btnCambiarFoto, btnGrabarAudio, btnDetenerAudio;
    ImageView imgFotoEdit;
    MapView mapEdit;

    SQLiteDatabase db;

    int id;

    double lat = 0, lon = 0;
    byte[] fotoBytes = null;
    byte[] audioBytes = null;

    boolean nuevaFoto = false;
    boolean nuevoAudio = false;

    MediaRecorder recorder;
    File archivoAudioTemp;

    Marker marker;


    ActivityResultLauncher<Intent> fotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_lista);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        inicializarUI();
        inicializarBD();
        inicializarMapa();
        inicializarFotoLauncher();
        cargarDatos();


        btnActualizar.setOnClickListener(v -> actualizarRegistro());
        btnEliminar.setOnClickListener(v -> eliminarRegistro());
        btnReproducirAudio.setOnClickListener(v -> reproducirAudio());
        btnCambiarFoto.setOnClickListener(v -> cambiarFoto());
        btnGrabarAudio.setOnClickListener(v -> iniciarGrabacion());
        btnDetenerAudio.setOnClickListener(v -> detenerGrabacion());
    }

    private void inicializarUI() {
        txtCodigoEdit = findViewById(R.id.txtCodigoEdit);
        txtNombreEdit = findViewById(R.id.txtNombreEdit);
        txtCedulaEdit = findViewById(R.id.txtCedulaEdit);
        txtLatEdit = findViewById(R.id.txtLatEdit);
        txtLonEdit = findViewById(R.id.txtLonEdit);

        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnReproducirAudio = findViewById(R.id.btnReproducirAudio);

        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnGrabarAudio = findViewById(R.id.btnGrabarAudio);
        btnDetenerAudio = findViewById(R.id.btnDetenerAudio);

        imgFotoEdit = findViewById(R.id.imgFotoEdit);
        mapEdit = findViewById(R.id.mapEdit);

        btnDetenerAudio.setVisibility(View.GONE);
    }

    private void inicializarBD() {
        DBGestion helper = new DBGestion(this, "BaseDatos", null, 2);
        db = helper.getWritableDatabase();

        id = getIntent().getIntExtra("id", -1);
        if (id == -1) {
            Toast.makeText(this, "Error al recibir ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void inicializarMapa() {
        mapEdit.setTileSource(TileSourceFactory.MAPNIK);
        mapEdit.setMultiTouchControls(true);

        marker = new Marker(mapEdit);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        MapEventsReceiver events = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                lat = p.getLatitude();
                lon = p.getLongitude();
                txtLatEdit.setText(String.valueOf(lat));
                txtLonEdit.setText(String.valueOf(lon));

                marker.setPosition(p);
                mapEdit.getOverlays().add(marker);
                mapEdit.invalidate();
                return true;
            }

            @Override public boolean longPressHelper(GeoPoint p) { return false; }
        };

        mapEdit.getOverlays().add(new MapEventsOverlay(events));
    }

    private void inicializarFotoLauncher() {
        fotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        imgFotoEdit.setImageBitmap(bitmap);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                        fotoBytes = bos.toByteArray();

                        nuevaFoto = true;
                    }
                }
        );
    }

    private void cargarDatos() {

        Cursor c = db.rawQuery(
                "SELECT nombre, cedula_empleado, latitud, longitud, foto, audio FROM ubicacion WHERE id=?",
                new String[]{String.valueOf(id)}
        );

        if (c.moveToFirst()) {

            txtCodigoEdit.setText(String.valueOf(id));
            txtNombreEdit.setText(c.getString(0));
            txtCedulaEdit.setText(c.getString(1));

            lat = c.getDouble(2);
            lon = c.getDouble(3);

            txtLatEdit.setText(String.valueOf(lat));
            txtLonEdit.setText(String.valueOf(lon));

            fotoBytes = c.getBlob(4);
            audioBytes = c.getBlob(5);

            if (fotoBytes != null)
                imgFotoEdit.setImageBitmap(BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length));

            if (lat != 0 && lon != 0) {
                GeoPoint punto = new GeoPoint(lat, lon);
                IMapController controller = mapEdit.getController();
                controller.setZoom(16.0);
                controller.setCenter(punto);

                marker.setPosition(punto);
                mapEdit.getOverlays().add(marker);
            }
        }
        c.close();
    }

    private void cambiarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fotoLauncher.launch(intent);
    }

    private void iniciarGrabacion() {
        try {
            btnGrabarAudio.setEnabled(false);
            btnDetenerAudio.setVisibility(View.VISIBLE);

            archivoAudioTemp = File.createTempFile("audio_nuevo", ".3gp", getCacheDir());

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(archivoAudioTemp.getAbsolutePath());

            recorder.prepare();
            recorder.start();

            Toast.makeText(this, "Grabando audio...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerGrabacion() {
        try {
            recorder.stop();
            recorder.release();
            recorder = null;

            btnGrabarAudio.setEnabled(true);
            btnDetenerAudio.setVisibility(View.GONE);

            InputStream is = new FileInputStream(archivoAudioTemp);
            audioBytes = is.readAllBytes();
            is.close();

            nuevoAudio = true;

            Toast.makeText(this, "Audio grabado correctamente", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error al detener grabación", Toast.LENGTH_SHORT).show();
        }
    }

    private void reproducirAudio() {

        if (audioBytes == null) {
            Toast.makeText(this, "No hay audio para reproducir", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File temp = File.createTempFile("audioTemp", ".3gp", getCacheDir());
            FileOutputStream fos = new FileOutputStream(temp);
            fos.write(audioBytes);
            fos.close();

            MediaPlayer player = new MediaPlayer();
            player.setDataSource(temp.getAbsolutePath());
            player.prepare();
            player.start();

        } catch (Exception e) {
            Toast.makeText(this, "Error al reproducir audio", Toast.LENGTH_SHORT).show();
        }
    }
    private void actualizarRegistro() {

        ContentValues valores = new ContentValues();

        valores.put("nombre", txtNombreEdit.getText().toString());
        valores.put("cedula_empleado", txtCedulaEdit.getText().toString());
        valores.put("latitud", txtLatEdit.getText().toString());
        valores.put("longitud", txtLonEdit.getText().toString());

        if (nuevaFoto)
            valores.put("foto", fotoBytes);

        if (nuevoAudio)
            valores.put("audio", audioBytes);

        db.update("ubicacion", valores, "id=?", new String[]{String.valueOf(id)});

        Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void eliminarRegistro() {
        db.execSQL("DELETE FROM ubicacion WHERE id=?", new Object[]{id});
        Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
        finish();
    }
}
