package com.example.hotelsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class HabitacionAdapter extends BaseAdapter {
    Context context;
    List<Habitacion> lst;

    public HabitacionAdapter(Context context, List<Habitacion> lista) {
        this.context = context;
        this.lst = lista;
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_habitacion, parent, false);

            holder = new ViewHolder();

            holder.imgHabitacion = convertView.findViewById(R.id.imgHabitacion);
            holder.txtCodigo = convertView.findViewById(R.id.txtCodigo);
            holder.txtNumero = convertView.findViewById(R.id.txtNumero);
            holder.txtEstado = convertView.findViewById(R.id.txtEstado);
            holder.txtPiso = convertView.findViewById(R.id.txtPiso);
            holder.txtNombre = convertView.findViewById(R.id.txtNombre);
            holder.txtDescripcion = convertView.findViewById(R.id.txtDescripcion);
            holder.txtPrecio = convertView.findViewById(R.id.txtPrecio);
            holder.txtCapacidad = convertView.findViewById(R.id.txtCapacidad);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Habitacion h = lst.get(position);

        holder.imgHabitacion.setImageResource(h.getImagen());
        holder.txtCodigo.setText("Código: " + h.getCodigo());
        holder.txtNumero.setText("Número: " + h.getNumero());
        holder.txtEstado.setText("Estado: " + h.getEstado());
        holder.txtPiso.setText("Piso: " + h.getPiso());
        holder.txtNombre.setText("Nombre: " + h.getNombre());
        holder.txtDescripcion.setText("Descripción: " + h.getDescripcion());
        holder.txtPrecio.setText("Precio/noche: " + h.getPrecioNoche());
        holder.txtCapacidad.setText("Capacidad: " + h.getCapacidad());

        return convertView;
    }

    static class ViewHolder {
        ImageView imgHabitacion;
        TextView txtCodigo, txtNumero, txtEstado, txtPiso, txtNombre, txtDescripcion, txtPrecio, txtCapacidad;
    }
}
