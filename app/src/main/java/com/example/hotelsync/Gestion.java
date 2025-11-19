package com.example.hotelsync;

public class Gestion {
    private String codigo;
    public int imagen;
    private String empleado;
    private String huesped;
    private String estado;
    private String fechaInicio;
    private String fechaFin;
    private String total;

    public Gestion(String codigo, int imagen, String empleado, String huesped, String estado, String fechaInicio, String fechaFin, String total) {
        this.codigo = codigo;
        this.imagen = imagen;
        this.empleado = empleado;
        this.huesped = huesped;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.total = total;
    }

    public String getCodigo() {
        return codigo;
    }
    public int getImagen() {
        return imagen;
    }

    public String getEmpleado() {
        return empleado;
    }

    public String getHuesped() {
        return huesped;
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

    public String getTotal() {
        return total;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public void setHuesped(String huesped) {
        this.huesped = huesped;
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

    public void setTotal(String total) {
        this.total = total;
    }
}
