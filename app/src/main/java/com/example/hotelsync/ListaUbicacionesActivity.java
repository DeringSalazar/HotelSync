package com.example.hotelsync;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListaUbicacionesActivity extends AppCompatActivity {

    ListView lista;
    SearchView searchView;
    Button btnEditar;

    SQLiteDatabase db;

    ArrayList<String> ubicaciones = new ArrayList<>();
    ArrayList<Integer> ids = new ArrayList<>();

    ArrayAdapter<String> adapter;

    int idSeleccionado = -1;
    int posicionSeleccionada = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ubicaciones);

        lista = findViewById(R.id.listUbicaciones2);
        searchView = findViewById(R.id.searchUbicaciones);
        btnEditar = findViewById(R.id.btnEditar);

        DBGestion helper = new DBGestion(this, "BaseDatos", null, 2);
        db = helper.getWritableDatabase();

        cargarUbicaciones();

        lista.setOnItemClickListener((parent, view, position, id) -> {
            idSeleccionado = ids.get(position);
            posicionSeleccionada = position;
            pintarSeleccion();
        });

        btnEditar.setOnClickListener(v -> {
            if (idSeleccionado == -1) {
                Toast.makeText(this, "Seleccione una ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, EditarUbicacionActivity.class);
            intent.putExtra("id", idSeleccionado);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override public boolean onQueryTextChange(String text) {
                adapter.getFilter().filter(text);
                idSeleccionado = -1;
                posicionSeleccionada = -1;
                lista.post(ListaUbicacionesActivity.this::pintarSeleccion);
                return true;
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
        ubicaciones.clear();
        ids.clear();

        Cursor c = db.rawQuery("SELECT id, nombre, latitud, longitud FROM ubicacion", null);

        while (c.moveToNext()) {
            int idUbicacion = c.getInt(0);
            ids.add(idUbicacion);

            String item =
                    "ID: " + idUbicacion +
                            "\nNombre: " + c.getString(1) +
                            "\nLat: " + c.getDouble(2) +
                            "   Lon: " + c.getDouble(3);

            ubicaciones.add(item);
        }

        c.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ubicaciones);
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
