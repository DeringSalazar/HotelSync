package com.example.hotelsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class UsuariosAdapter extends BaseAdapter {
    Context context;
    List<Vista> lst;

    public UsuariosAdapter(Context context, List<Vista> lst) {
        this.context = context;
        this.lst = lst;
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int i) {
        return lst.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        ViewHolder holder;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_usuarios, parent, false);

            holder = new ViewHolder();
            holder.imgUsuario = view.findViewById(R.id.imgUsuario);
            holder.txtNombreApellido = view.findViewById(R.id.txtNombreApellido);
            holder.txtCedula = view.findViewById(R.id.txtCedula);
            holder.txtTelefono = view.findViewById(R.id.txtTelefono);
            holder.txtCorreo = view.findViewById(R.id.txtCorreo);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Vista v = lst.get(i);

        holder.imgUsuario.setImageResource(v.getImagen());
        holder.txtNombreApellido.setText(v.getNombre() + " " + v.getApellido());
        holder.txtCedula.setText("Cédula: " + v.getCedula());
        holder.txtTelefono.setText("Tel: " + v.getTelefono());
        holder.txtCorreo.setText(v.getCorreo());

        return view;
    }

    static class ViewHolder {
        ImageView imgUsuario;
        TextView txtNombreApellido;
        TextView txtCedula;
        TextView txtTelefono;
        TextView txtCorreo;
    }
}
