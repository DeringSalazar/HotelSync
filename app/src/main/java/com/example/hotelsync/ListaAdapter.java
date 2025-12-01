package com.example.hotelsync;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListaAdapter extends BaseAdapter {

    Context context;
    ArrayList<ListaView> lista;

    public ListaAdapter(Context context, ArrayList<ListaView> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return lista.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.listview_lista, parent, false);
        }

        ListaView item = lista.get(position);

        ImageView img = convertView.findViewById(R.id.imgFotoItem);
        TextView txtId = convertView.findViewById(R.id.txtItemId);
        TextView txtNombre = convertView.findViewById(R.id.txtItemNombre);
        TextView txtLatLon = convertView.findViewById(R.id.txtItemLatLon);

        txtId.setText("ID: " + item.id);
        txtNombre.setText(item.nombre);
        txtLatLon.setText("Lat: " + item.lat + "   Lon: " + item.lon);

        if (item.foto != null)
            img.setImageBitmap(BitmapFactory.decodeByteArray(item.foto, 0, item.foto.length));
        else
            img.setImageResource(android.R.drawable.ic_menu_camera);

        return convertView;
    }
}

