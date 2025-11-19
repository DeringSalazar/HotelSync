package com.example.hotelsync;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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
        holder.txtIdReserva.setText(r.getIdReserva());
        holder.txtCedula.setText(context.getString(R.string.c_dula) + r.getCedulaHuesped());
        holder.txtNombre.setText(context.getString(R.string.nombre) + r.getNombreHuesped());
        holder.txtEstado.setText(context.getString(R.string.estado) + r.getEstado());
        holder.txtInicio.setText(context.getString(R.string.inicio) + r.getFechaInicio());
        holder.txtFin.setText(context.getString(R.string.fin) + r.getFechaFin());

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
