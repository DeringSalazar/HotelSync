package com.example.hotelsync;

public class Vista {
    public int cedula;
    public int imagen;
    public String nombre;
    public String apellido;
    public int telefono;
    public String correo;

    public Vista(int cedula, int imagen, String nombre, String apellido, int telefono, String correo){
        this.cedula = cedula;
        this.imagen = imagen;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }
    public int getCedula() {
        return cedula;
    }

    public int getImagen() {
        return imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public int getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
