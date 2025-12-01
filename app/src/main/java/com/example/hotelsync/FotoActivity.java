package com.example.hotelsync;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

public class FotoActivity extends AppCompatActivity {

    ImageView imgFoto;
    EditText txtCodigo, txtNombre, txtEmpleado;
    Button btnTomarFoto, btnGuardar, btnCancelar, btnGrabar, btnReproducir, btnDetener;
    MediaRecorder grabador;
    MediaPlayer reproductor;
    String rutaAudio = "";
    MapView mapView;
    Marker markerMapa;
    double latitud = 0, longitud = 0;
    ActivityResultLauncher<Intent> lanzadorTomarFoto;
    private Bitmap imagenBitmap;
    byte[] fotoBytes = null;
    byte[] audioBytes = null;
    SQLiteDatabase sql;
    private static final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        Configuration.getInstance().setUserAgentValue(getPackageName());

        imgFoto = findViewById(R.id.imgFoto);
        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtEmpleado = findViewById(R.id.txtEmpleado);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnGrabar = findViewById(R.id.btnGrabar);
        btnDetener = findViewById(R.id.btnDetener);
        btnReproducir = findViewById(R.id.btnReproducir);
        mapView = findViewById(R.id.mapView);

        configurarMapa();
        pedirPermisos();

        lanzadorTomarFoto = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        imagenBitmap = (Bitmap) resultado.getData().getExtras().get("data");
                        imgFoto.setImageBitmap(imagenBitmap);
                        fotoBytes = bitmapToBytes(imagenBitmap);
                    }
                }
        );

        DBGestion helper = new DBGestion(this, "BaseDatos", null, 2);
        sql = helper.getWritableDatabase();

        btnGrabar.setOnClickListener(v -> iniciarGrabacion());
        btnReproducir.setOnClickListener(v -> reproducirAudio());
        btnDetener.setOnClickListener(v -> detenerGrabacion());
        btnGuardar.setOnClickListener(v -> guardarEnBD());
        btnCancelar.setOnClickListener(v -> limpiarTodo());
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

    public void Volver(View view) {
        Intent intent = new Intent(this, EditarListaDatos.class);
        startActivity(intent);
    }

    public void tomarFoto(View vista) {
        Intent intentTomarFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        lanzadorTomarFoto.launch(intentTomarFoto);
    }
    private byte[] bitmapToBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    private void configurarMapa() {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController controller = mapView.getController();
        controller.setZoom(16.0);
        controller.setCenter(new GeoPoint(10.0, -84.0)); // punto inicial

        markerMapa = new Marker(mapView);
        markerMapa.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        MapEventsReceiver eventos = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                latitud = p.getLatitude();
                longitud = p.getLongitude();

                markerMapa.setPosition(p);
                mapView.getOverlays().add(markerMapa);
                mapView.invalidate();

                Toast.makeText(FotoActivity.this,
                        "Marcador: " + latitud + ", " + longitud,
                        Toast.LENGTH_SHORT).show();

                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) { return false; }
        };

        mapView.getOverlays().add(new MapEventsOverlay(eventos));
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

            audioBytes = fileToBytes(rutaAudio);

        } catch (Exception e) {
            Toast.makeText(this, "Error al detener", Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] fileToBytes(String path) {
        try {
            File file = new File(path);
            byte[] bytes = new byte[(int) file.length()];
            java.io.FileInputStream fis = new java.io.FileInputStream(file);
            fis.read(bytes);
            fis.close();
            return bytes;
        } catch (Exception e) {
            return null;
        }
    }

    private void reproducirAudio() {
        if (rutaAudio == null || rutaAudio.isEmpty()) {
            Toast.makeText(this, "No hay audio", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            reproductor = new MediaPlayer();
            reproductor.setDataSource(rutaAudio);
            reproductor.prepare();
            reproductor.start();
        } catch (Exception e) {
            Toast.makeText(this, "No se puede reproducir", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarEnBD() {
        if (latitud == 0 && longitud == 0) {
            Toast.makeText(this, "Debe seleccionar un punto en el mapa", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues cv = new ContentValues();
        cv.put("id", txtCodigo.getText().toString());
        cv.put("nombre", txtNombre.getText().toString());
        cv.put("cedula_empleado", txtEmpleado.getText().toString());
        cv.put("latitud", String.valueOf(latitud));
        cv.put("longitud", String.valueOf(longitud));
        cv.put("foto", fotoBytes);
        cv.put("audio", audioBytes);

        long resultado = sql.insert("ubicacion", null, cv);

        if (resultado != -1) {
            Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
            limpiarTodo();
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarTodo() {

        txtCodigo.setText("");
        txtNombre.setText("");
        txtEmpleado.setText("");

        imgFoto.setImageResource(0);
        fotoBytes = null;

        rutaAudio = "";
        audioBytes = null;

        mapView.getOverlays().remove(markerMapa);
        mapView.invalidate();

        latitud = 0;
        longitud = 0;

        Toast.makeText(this, "Formulario limpiado", Toast.LENGTH_SHORT).show();
    }
}
