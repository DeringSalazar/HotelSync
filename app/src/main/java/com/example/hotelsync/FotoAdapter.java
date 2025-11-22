package com.example.hotelsync;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class FotoAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ItemFoto> lista;

    public FotoAdapter(Context context, ArrayList<ItemFoto> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.listview_foto, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.imagenFoto);

        ItemFoto item = lista.get(position);

        Bitmap bitmap = BitmapFactory.decodeByteArray(item.getFoto(), 0, item.getFoto().length);
        imageView.setImageBitmap(bitmap);

        return convertView;
    }
}
