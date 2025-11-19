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

    private final Context context;
    private final List<Habitacion> habitaciones;

    public HabitacionAdapter(Context context, List<Habitacion> habitaciones) {
        this.context = context;
        this.habitaciones = habitaciones;
    }

    @Override
    public int getCount() {
        return habitaciones.size();
    }

    @Override
    public Habitacion getItem(int position) {
        return habitaciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.listview_habitacion, parent, false);

            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Habitacion h = getItem(position);
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
        TextView txtCodigo, txtNumero, txtEstado, txtPiso,
                txtNombre, txtDescripcion, txtPrecio, txtCapacidad;

        ViewHolder(View view) {
            imgHabitacion = view.findViewById(R.id.imgHabitacion);
            txtCodigo = view.findViewById(R.id.txtCodigo);
            txtNumero = view.findViewById(R.id.txtNumero);
            txtEstado = view.findViewById(R.id.txtEstado);
            txtPiso = view.findViewById(R.id.txtPiso);
            txtNombre = view.findViewById(R.id.txtNombre);
            txtDescripcion = view.findViewById(R.id.txtDescripcion);
            txtPrecio = view.findViewById(R.id.txtPrecio);
            txtCapacidad = view.findViewById(R.id.txtCapacidad);
        }
    }

    public void actualizarLista(List<Habitacion> nuevaLista) {
        habitaciones.clear();
        habitaciones.addAll(nuevaLista);
        notifyDataSetChanged();
    }
}
