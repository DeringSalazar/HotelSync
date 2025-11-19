package com.example.hotelsync;

public class ReservasHuesped {

    private String idReserva;
    private int imagen = R.drawable.habitacion;
    private String cedulaHuesped;
    private String nombreHuesped;
    private String estado;
    private String fechaInicio;
    private String fechaFin;
    private String habitacion;

    public ReservasHuesped() {}

    public ReservasHuesped(String idReserva, int imagen, String cedulaHuesped, String nombreHuesped, String estado, String fechaInicio, String fechaFin, String habitacion) {

        this.idReserva = idReserva;
        this.imagen = imagen;
        this.cedulaHuesped = cedulaHuesped;
        this.nombreHuesped = nombreHuesped;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.habitacion = habitacion;
    }

    public String getIdReserva() { return idReserva; }
    public void setIdReserva(String idReserva) { this.idReserva = idReserva; }

    public int getImagen() { return imagen; }
    public void setImagen(int imagen) { this.imagen = imagen; }

    public String getCedulaHuesped() { return cedulaHuesped; }
    public void setCedulaHuesped(String cedulaHuesped) { this.cedulaHuesped = cedulaHuesped; }

    public String getNombreHuesped() { return nombreHuesped; }
    public void setNombreHuesped(String nombreHuesped) { this.nombreHuesped = nombreHuesped; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getHabitacion() { return habitacion; }
    public void setHabitacion(String habitacion) { this.habitacion = habitacion; }

}
