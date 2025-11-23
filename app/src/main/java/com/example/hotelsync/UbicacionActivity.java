package com.example.hotelsync;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

public class UbicacionActivity extends AppCompatActivity {

    private MapView mapView;
    private EditText txtLatitud, txtLongitud, txtNombre;
    private Button btnGuardar;

    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_ubicacion);

        txtLatitud = findViewById(R.id.txtLatitud);
        txtLongitud = findViewById(R.id.txtLongitud);
        txtNombre = findViewById(R.id.txtNombre);
        btnGuardar = findViewById(R.id.btnGuardar);
        mapView = findViewById(R.id.map);

        DBGestion gestion = new DBGestion(this, "hotel.db", null, 1);
        db = gestion.getWritableDatabase();

        // Config mapa
        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);

        GeoPoint start = new GeoPoint(10.430684188597372, -85.08498580135634);
        mapView.getController().setCenter(start);

        Marker marker = new Marker(mapView);
        marker.setPosition(start);
        marker.setTitle("Ubicación inicial");
        mapView.getOverlays().add(marker);

        txtLatitud.setText(String.valueOf(start.getLatitude()));
        txtLongitud.setText(String.valueOf(start.getLongitude()));

        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                actualizarMarcador(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                actualizarMarcador(p);
                return true;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mReceiver);
        mapView.getOverlays().add(mapEventsOverlay);

        btnGuardar.setOnClickListener(v -> guardarUbicacion());
    }

    private void actualizarMarcador(GeoPoint punto) {
        txtLatitud.setText(String.valueOf(punto.getLatitude()));
        txtLongitud.setText(String.valueOf(punto.getLongitude()));

        mapView.getOverlays().clear();

        Marker nuevo = new Marker(mapView);
        nuevo.setPosition(punto);
        nuevo.setTitle("Ubicación seleccionada");
        mapView.getOverlays().add(nuevo);

        mapView.invalidate();
    }

    private void guardarUbicacion() {
        String nombre = txtNombre.getText().toString().trim();
        String lat = txtLatitud.getText().toString().trim();
        String lon = txtLongitud.getText().toString().trim();

        if (nombre.isEmpty() || lat.isEmpty() || lon.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("latitud", lat);
        values.put("longitud", lon);

        long result = db.insert("ubicacion", null, values);

        if (result > 0) {
            Toast.makeText(this, "Ubicación guardada", Toast.LENGTH_SHORT).show();
            txtNombre.setText("");
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        }
    }
}
