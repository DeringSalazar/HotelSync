package com.example.hotelsync;

import android.content.ContentValues;
import android.database.Cursor;
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

public class EditarUbicacionActivity extends AppCompatActivity {

    EditText txtNombreEdit, txtLatEdit, txtLonEdit;
    Button btnActualizar, btnEliminar;

    MapView mapView;
    Marker marker;

    SQLiteDatabase db;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_editar_ubicacion);

        txtNombreEdit = findViewById(R.id.txtNombreEdit);
        txtLatEdit = findViewById(R.id.txtLatEdit);
        txtLonEdit = findViewById(R.id.txtLonEdit);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        mapView = findViewById(R.id.mapEdit);

        DBGestion gestion = new DBGestion(this, "hotel.db", null, 1);
        db = gestion.getWritableDatabase();

        id = getIntent().getIntExtra("id", -1);

        mapView.setMultiTouchControls(true);
        mapView.getController().setZoom(16.0);

        cargarDatos();

        MapEventsReceiver mReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                moverMarcador(p);
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                moverMarcador(p);
                return true;
            }
        };

        MapEventsOverlay mapEventsOverlay = new MapEventsOverlay(mReceiver);
        mapView.getOverlays().add(mapEventsOverlay);

        btnActualizar.setOnClickListener(v -> actualizar());
        btnEliminar.setOnClickListener(v -> eliminar());
    }

    private void cargarDatos() {
        Cursor cursor = db.rawQuery("SELECT nombre, latitud, longitud FROM ubicacion WHERE id=?",
                new String[]{String.valueOf(id)});

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(0);
            String lat = cursor.getString(1);
            String lon = cursor.getString(2);

            txtNombreEdit.setText(nombre);
            txtLatEdit.setText(lat);
            txtLonEdit.setText(lon);

            GeoPoint punto = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));
            mapView.getController().setCenter(punto);

            marker = new Marker(mapView);
            marker.setPosition(punto);
            marker.setTitle("Ubicación guardada");
            mapView.getOverlays().add(marker);
        }

        cursor.close();
    }

    private void moverMarcador(GeoPoint punto) {
        txtLatEdit.setText(String.valueOf(punto.getLatitude()));
        txtLonEdit.setText(String.valueOf(punto.getLongitude()));

        mapView.getOverlays().remove(marker);

        marker = new Marker(mapView);
        marker.setPosition(punto);
        marker.setTitle("Nueva ubicación");
        mapView.getOverlays().add(marker);

        mapView.invalidate();
    }

    private void actualizar() {
        ContentValues v = new ContentValues();
        v.put("nombre", txtNombreEdit.getText().toString().trim());
        v.put("latitud", txtLatEdit.getText().toString().trim());
        v.put("longitud", txtLonEdit.getText().toString().trim());

        int filas = db.update("ubicacion", v, "id=?", new String[]{String.valueOf(id)});

        if (filas > 0) {
            Toast.makeText(this, "Ubicación actualizada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminar() {
        int filas = db.delete("ubicacion", "id=?", new String[]{String.valueOf(id)});

        if (filas > 0) {
            Toast.makeText(this, "Ubicación eliminada", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
        }
    }
}
