package com.example.hotelsync;

public class ListaView {
    public int id;
    public String nombre;
    public double lat;
    public double lon;
    public byte[] foto;

    public ListaView(int id, String nombre, double lat, double lon, byte[] foto) {
        this.id = id;
        this.nombre = nombre;
        this.lat = lat;
        this.lon = lon;
        this.foto = foto;
    }
}
