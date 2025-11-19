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
        holder.txtCodigo.setText(context.getString(R.string.c_digo) + h.getCodigo());
        holder.txtNumero.setText(context.getString(R.string.n_mero) + h.getNumero());
        holder.txtEstado.setText(context.getString(R.string.estado2) + h.getEstado());
        holder.txtPiso.setText(context.getString(R.string.piso) + h.getPiso());
        holder.txtNombre.setText(context.getString(R.string.nombre2) + h.getNombre());
        holder.txtDescripcion.setText(context.getString(R.string.descripci_n) + h.getDescripcion());
        holder.txtPrecio.setText(context.getString(R.string.precio_noche) + h.getPrecioNoche());
        holder.txtCapacidad.setText(context.getString(R.string.capacidad) + h.getCapacidad());

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
