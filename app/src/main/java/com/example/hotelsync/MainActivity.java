package com.example.hotelsync;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText TxtCedula, TxtContra;
    RadioButton BtnHuesped, BtnEmpleado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TxtCedula = findViewById(R.id.TxtCedula);
        TxtContra = findViewById(R.id.TxtContra);
        BtnHuesped = findViewById(R.id.BtnHuesped);
        BtnEmpleado = findViewById(R.id.BtnEmpleado);
    }

    public void Siguiente(View view)  {
        Intent intent= new Intent(this,Registro.class);
        // intent.putExtra("texto",TxtUno.getText().toString());
        startActivity(intent);

    }
}