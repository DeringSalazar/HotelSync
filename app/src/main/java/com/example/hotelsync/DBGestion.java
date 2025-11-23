package com.example.hotelsync;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBGestion extends SQLiteOpenHelper {

    public DBGestion(@Nullable Context context, @Nullable String name,
                     @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE huesped (" + "cedula TEXT PRIMARY KEY, " + "nombre TEXT, " + "apellido TEXT, " + "telefono TEXT, " + "correo TEXT)");
        db.execSQL("CREATE TABLE empleado (" + "cedula TEXT PRIMARY KEY, " + "nombre TEXT, " + "apellido TEXT, " + "telefono TEXT, " + "correo TEXT)");
        db.execSQL("CREATE TABLE habitacion (" + "codigo TEXT PRIMARY KEY, " + "numero TEXT, " + "estado TEXT, " + "piso TEXT, " + "nombre TEXT, " + "descripcion TEXT, " + "precio_noche TEXT, " + "capacidad TEXT)");
        db.execSQL("CREATE TABLE reserva (" + "id_reserva TEXT PRIMARY KEY, " + "cedula_empleado TEXT REFERENCES empleado(cedula), " + "codigo_habitacion TEXT REFERENCES habitacion(codigo), " + "fecha_inicio TEXT, " + "fecha_fin TEXT, " + "total TEXT)");

        db.execSQL("CREATE TABLE reserva_huesped (" + "idreserva TEXT, " + "cedula_huesped TEXT, " + "estado TEXT, " +
                "PRIMARY KEY (idreserva, cedula_huesped), " +
                "FOREIGN KEY (idreserva) REFERENCES reserva(id_reserva), " +
                "FOREIGN KEY (cedula_huesped) REFERENCES huesped(cedula))");

        db.execSQL("CREATE TABLE ubicacion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "descripcion TEXT, " +
                "latitud TEXT, " +
                "longitud TEXT)");

        db.execSQL("CREATE TABLE multimedia_habitacion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codigo_habit TEXT, " +
                "foto BLOB, " +
                "FOREIGN KEY(codigo_habit) REFERENCES habitacion(codigo))");

        db.execSQL("CREATE TABLE audio_habitacion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "codigo_habit TEXT, " +
                "audio BLOB, " +
                "FOREIGN KEY(codigo_habit) REFERENCES habitacion(codigo))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS audio_habitacion");
        db.execSQL("DROP TABLE IF EXISTS multimedia_habitacion");
        db.execSQL("DROP TABLE IF EXISTS reserva_huesped");
        db.execSQL("DROP TABLE IF EXISTS reserva");
        db.execSQL("DROP TABLE IF EXISTS ubicacion");
        db.execSQL("DROP TABLE IF EXISTS habitacion");
        db.execSQL("DROP TABLE IF EXISTS empleado");
        db.execSQL("DROP TABLE IF EXISTS huesped");
        onCreate(db);
    }
}
