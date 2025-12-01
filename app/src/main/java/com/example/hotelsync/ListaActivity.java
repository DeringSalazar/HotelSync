package com.example.hotelsync;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListaActivity extends AppCompatActivity {

    ListView lista;
    SearchView searchView;
    Button btnEditar;

    SQLiteDatabase db;

    ArrayList<ListaView> listaUbicaciones = new ArrayList<>();
    ListaAdapter adapter;

    int idSeleccionado = -1;
    int posicionSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        lista = findViewById(R.id.listUbicaciones2);
        searchView = findViewById(R.id.searchUbicaciones);
        btnEditar = findViewById(R.id.btnEditar);

        DBGestion helper = new DBGestion(this, "BaseDatos", null, 2);
        db = helper.getWritableDatabase();

        cargarUbicaciones();

        lista.setOnItemClickListener((parent, view, position, id) -> {
            ListaView item = (ListaView) adapter.getItem(position);
            idSeleccionado = item.id;
            posicionSeleccionada = position;
            pintarSeleccion();
        });

        btnEditar.setOnClickListener(v -> {
            if (idSeleccionado == -1) {
                Toast.makeText(this, "Seleccione una ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, EditarListaDatos.class);
            intent.putExtra("id", idSeleccionado);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String text) {

                ArrayList<ListaView> filtrada = new ArrayList<>();

                for (ListaView u : listaUbicaciones) {

                    if (String.valueOf(u.id).contains(text) ||
                            u.nombre.toLowerCase().contains(text.toLowerCase())) {

                        filtrada.add(u);
                    }
                }

                adapter = new ListaAdapter(ListaActivity.this, filtrada);
                lista.setAdapter(adapter);

                idSeleccionado = -1;
                posicionSeleccionada = -1;
                lista.post(ListaActivity.this::pintarSeleccion);

                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUbicaciones();
        idSeleccionado = -1;
        posicionSeleccionada = -1;
    }

    private void cargarUbicaciones() {

        listaUbicaciones.clear();

        Cursor c = db.rawQuery("SELECT id, nombre, latitud, longitud, foto FROM ubicacion", null);

        while (c.moveToNext()) {

            int id = c.getInt(0);
            String nombre = c.getString(1);
            double lat = c.getDouble(2);
            double lon = c.getDouble(3);
            byte[] foto = c.getBlob(4);

            listaUbicaciones.add(new ListaView(id, nombre, lat, lon, foto));
        }

        c.close();

        adapter = new ListaAdapter(this, listaUbicaciones);
        lista.setAdapter(adapter);

        lista.post(this::pintarSeleccion);
    }

    private void pintarSeleccion() {
        for (int i = 0; i < lista.getChildCount(); i++) {
            if (i == posicionSeleccionada)
                lista.getChildAt(i).setBackgroundColor(0xFFB2DFDB);
            else
                lista.getChildAt(i).setBackgroundColor(0xFFFFFFFF);
        }
    }
}
