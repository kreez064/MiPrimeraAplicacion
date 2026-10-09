package com.example.miprimeraaplicacion.views

import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.miprimeraaplicacion.R
import com.google.android.material.materialswitch.MaterialSwitch

class PreferenciasActivity : AppCompatActivity() {

    private lateinit var swVentilacion: MaterialSwitch
    private lateinit var spSector: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_preferencias)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        swVentilacion = findViewById(R.id.swVentilacion)
        spSector = findViewById(R.id.spSector)

        // Escuchador de cambio de estado para el switch de ventilación
        swVentilacion.setOnCheckedChangeListener { _, isChecked ->
            val mensaje = if (isChecked) "Ventilación automática activada" else "Ventilación automática desactivada"
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }
}