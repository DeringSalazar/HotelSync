package com.example.hotelsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.*;

import java.util.List;

public class ReservasAdapter extends BaseAdapter {
    Context context;
    List<Gestion> lst;

    public ReservasAdapter(Context context, List<Gestion> lst) {
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
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_gestion, parent, false);

            holder = new ViewHolder();

            holder.imgHabitacion = convertView.findViewById(R.id.imgHabitacion);
            holder.txtCodigo = convertView.findViewById(R.id.txtCodigo);
            holder.txtEmpleado = convertView.findViewById(R.id.txtEmpleado);
            holder.txtHuesped = convertView.findViewById(R.id.txtHuesped);
            holder.txtEstado = convertView.findViewById(R.id.txtEstado);
            holder.txtFechaInicio = convertView.findViewById(R.id.txtFechaInicio);
            holder.txtFechaFin = convertView.findViewById(R.id.txtFechaFin);
            holder.txtTotal = convertView.findViewById(R.id.txtTotal);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Gestion g = lst.get(position);

        holder.imgHabitacion.setImageResource(g.getImagen());
        holder.txtCodigo.setText("Código: " + g.getCodigo());
        holder.txtEmpleado.setText("Empleado: " + g.getEmpleado());
        holder.txtHuesped.setText("Huésped: " + g.getHuesped());
        holder.txtEstado.setText("Estado: " + g.getEstado());
        holder.txtFechaInicio.setText("Inicio: " + g.getFechaInicio());
        holder.txtFechaFin.setText("Fin: " + g.getFechaFin());
        holder.txtTotal.setText("Total: " + g.getTotal());

        return convertView;
    }

    // --- ViewHolder ---
    static class ViewHolder {
        ImageView imgHabitacion;
        TextView txtCodigo;
        TextView txtEmpleado;
        TextView txtHuesped;
        TextView txtEstado;
        TextView txtFechaInicio;
        TextView txtFechaFin;
        TextView txtTotal;
    }

}
