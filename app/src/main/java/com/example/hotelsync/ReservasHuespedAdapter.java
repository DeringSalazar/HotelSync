package com.example.hotelsync;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.hotelsync.ReservasHuesped;

import java.util.List;

public class ReservasHuespedAdapter extends BaseAdapter {

    Context context;
    List<ReservasHuesped> lst;

    public ReservasHuespedAdapter(Context context, List<ReservasHuesped> lst) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_reservas_huesped, parent, false);

            holder = new ViewHolder();
            holder.imgIcono = convertView.findViewById(R.id.imgIcono);
            holder.txtIdReserva = convertView.findViewById(R.id.txtIdReserva);
            holder.txtCedula = convertView.findViewById(R.id.txtCedula);
            holder.txtNombre = convertView.findViewById(R.id.txtNombre);
            holder.txtEstado = convertView.findViewById(R.id.txtEstado);
            holder.txtInicio = convertView.findViewById(R.id.txtInicio);
            holder.txtFin = convertView.findViewById(R.id.txtFin);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReservasHuesped r = lst.get(position);

        holder.imgIcono.setImageResource(r.getImagen());
        holder.txtIdReserva.setText("Reserva: " + r.getIdReserva());
        holder.txtCedula.setText("Cédula: " + r.getCedulaHuesped());
        holder.txtNombre.setText("Nombre: " + r.getNombreHuesped());
        holder.txtEstado.setText("Estado: " + r.getEstado());
        holder.txtInicio.setText("Inicio: " + r.getFechaInicio());
        holder.txtFin.setText("Fin: " + r.getFechaFin());

        return convertView;
    }

    static class ViewHolder {
        ImageView imgIcono;
        TextView txtIdReserva;
        TextView txtCedula;
        TextView txtNombre;
        TextView txtEstado;
        TextView txtInicio;
        TextView txtFin;
    }
}
