package com.example.hotelsync;

public class Habitacion {
    private String codigo;
    private String numero;
    private String estado;
    private String piso;
    private String nombre;
    private String descripcion;
    private String precioNoche;
    private String capacidad;
    private int imagen;

    public Habitacion(String codigo, String numero, String estado, String piso, String nombre, String descripcion, String precioNoche, String capacidad, int imagen) {
        this.codigo = codigo;
        this.numero = numero;
        this.estado = estado;
        this.piso = piso;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioNoche = precioNoche;
        this.capacidad = capacidad;
        this.imagen = imagen;
    }

    // GETTERS
    public String getCodigo() {
        return codigo;
    }
    public String getNumero() {
        return numero;
    }
    public String getEstado() {
        return estado;
    }
    public String getPiso() {
        return piso;
    }
    public String getNombre() {
        return nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getPrecioNoche() {
        return precioNoche;
    }
    public String getCapacidad() {
        return capacidad;
    }
    public int getImagen() {
        return imagen;
    }

    // SETTERS
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public void setPiso(String piso) {
        this.piso = piso;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setPrecioNoche(String precioNoche) {
        this.precioNoche = precioNoche;
    }
    public void setCapacidad(String capacidad) {
        this.capacidad = capacidad;
    }
    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}
