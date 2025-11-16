package com.example.hotelsync;

public class ReservasHuesped {

    private String idReserva;
    private int imagen;
    private String cedulaHuesped;
    private String nombreHuesped;
    private String estado;
    private String fechaInicio;
    private String fechaFin;

    public ReservasHuesped(String idReserva, int imagen, String cedulaHuesped,
                           String nombreHuesped, String estado, String fechaInicio,
                           String fechaFin) {

        this.idReserva = idReserva;
        this.imagen = imagen;
        this.cedulaHuesped = cedulaHuesped;
        this.nombreHuesped = nombreHuesped;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getIdReserva() {
        return idReserva;
    }

    public int getImagen() {
        return imagen;
    }

    public String getCedulaHuesped() {
        return cedulaHuesped;
    }

    public String getNombreHuesped() {
        return nombreHuesped;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setIdReserva(String idReserva) {
        this.idReserva = idReserva;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public void setCedulaHuesped(String cedulaHuesped) {
        this.cedulaHuesped = cedulaHuesped;
    }

    public void setNombreHuesped(String nombreHuesped) {
        this.nombreHuesped = nombreHuesped;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }
}
