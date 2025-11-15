package com.example.hotelsync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBGestion extends SQLiteOpenHelper {

    public DBGestion(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table huesped (cedula text primary key, nombre text, apellido text, telefono text, correo text)");
        db.execSQL("create table empleado (cedula text primary key, nombre text, apellido text, telefono text, correo text)");
        db.execSQL("create table habitacion (codigo text primary key, numero text, estado text, piso text, nombre text, descripcion text, precio_noche text, capacidad text)");
        db.execSQL("create table reserva (" +
                "id_reserva text primary key, " +
                "cedula_empleado text references empleado(cedula), " +
                "codigo_habitacion text references habitacion(codigo), " +
                "fecha_inicio text, " +
                "fecha_fin text, " +
                "Total text)");
        db.execSQL("create table reserva_huesped (" +
                "idreserva text, " +
                "cedula_huesped text, " +
                "estado text, " +
                "PRIMARY KEY (idreserva, cedula_huesped), " +
                "FOREIGN KEY (idreserva) REFERENCES reserva(id_reserva), " +
                "FOREIGN KEY (cedula_huesped) REFERENCES huesped(cedula))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
