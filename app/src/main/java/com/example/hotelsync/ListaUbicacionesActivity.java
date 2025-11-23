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

    int idSeleccionado = -1;      // ID de DB
    int posicionSeleccionada = -1; // posición en la lista (para el color)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ubicaciones);

        lista = findViewById(R.id.listUbicaciones2);
        searchView = findViewById(R.id.searchUbicaciones);
        btnEditar = findViewById(R.id.btnEditar);

        DBGestion gestion = new DBGestion(this, "hotel.db", null, 1);
        db = gestion.getWritableDatabase();

        cargarUbicaciones();

        // 👉 Seleccionar item sin Toast, solo resaltado
        lista.setOnItemClickListener((parent, view, position, id) -> {
            idSeleccionado = ids.get(position);
            posicionSeleccionada = position;

            pintarSeleccion(); // 👉 Resaltar selección
        });

        // 👉 Botón EDITAR abre la pantalla SOLO si hay selección
        btnEditar.setOnClickListener(v -> {
            if (idSeleccionado == -1) {
                Toast.makeText(this, "Seleccione una ubicación primero", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, EditarUbicacionActivity.class);
                intent.putExtra("id", idSeleccionado);
                startActivity(intent);
            }
        });

        // 🔍 FILTRO EN TIEMPO REAL
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);

                // Reset selección si cambia la búsqueda
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

        Cursor cursor = db.rawQuery("SELECT id, nombre, latitud, longitud FROM ubicacion", null);

        while (cursor.moveToNext()) {
            ids.add(cursor.getInt(0));
            String item = cursor.getString(1) +
                    "\nLat: " + cursor.getString(2) +
                    "  Lon: " + cursor.getString(3);
            ubicaciones.add(item);
        }

        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ubicaciones);
        lista.setAdapter(adapter);

        // 👉 repinta por si quedó algo marcado antes
        lista.post(this::pintarSeleccion);
    }

    // ⭐ Pintar el item seleccionado
    private void pintarSeleccion() {
        for (int i = 0; i < lista.getChildCount(); i++) {
            if (i == posicionSeleccionada) {
                lista.getChildAt(i).setBackgroundColor(0xFFB2DFDB); // verde agua suave
            } else {
                lista.getChildAt(i).setBackgroundColor(0xFFFFFFFF); // blanco
            }
        }
    }
}
