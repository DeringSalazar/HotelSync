package com.example.hotelsync;

public class ItemFoto {
    private int id;
    private byte[] foto;

    public ItemFoto(int id, byte[] foto) {
        this.id = id;
        this.foto = foto;
    }

    public int getId() { return id; }
    public byte[] getFoto() { return foto; }

    public void setId(int id) {
        this.id = id;
    }
    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
