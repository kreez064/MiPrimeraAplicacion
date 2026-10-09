package com.example.miprimeraaplicacion.views

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miprimeraaplicacion.R
import com.example.miprimeraaplicacion.models.RegistroInvernadero
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FormularioLecturaActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var txtTituloFormulario: TextView
    private lateinit var edtUbicacion: EditText
    private lateinit var edtTemperatura: EditText
    private lateinit var edtHumedad: EditText

    // Variable para saber si estamos editando (contendrá el ID del documento) o creando (null)
    private var registroIdEditar: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario_lectura)

        db = Firebase.firestore

        txtTituloFormulario = findViewById(R.id.txtTituloFormulario)
        edtUbicacion = findViewById(R.id.edtUbicacion)
        edtTemperatura = findViewById(R.id.edtTemperatura)
        edtHumedad = findViewById(R.id.edtHumedad)

        // Revisamos si el Intent trae datos adjuntos para Modo Edición
        registroIdEditar = intent.getStringExtra("registroId")

        if (registroIdEditar != null) {
            // MODO EDITAR
            txtTituloFormulario.text = "Editar Registro Invernadero"
            edtUbicacion.setText(intent.getStringExtra("registroUbicacion"))
            val temp = intent.getDoubleExtra("registroTemperatura", 0.0)
            val hum = intent.getDoubleExtra("registroHumedad", 0.0)
            edtTemperatura.setText(temp.toString())
            edtHumedad.setText(hum.toString())
        } else {
            // MODO CREAR
            txtTituloFormulario.text = "Nuevo Registro Invernadero"
        }
    }

    fun onGuardarRegistroClick(view: View) {
        val ubicacion = edtUbicacion.text.toString().trim()
        val tempString = edtTemperatura.text.toString().trim()
        val humString = edtHumedad.text.toString().trim()

        // 1. Validaciones para evitar guardar si hay campos vacíos
        if (ubicacion.isEmpty()) {
            edtUbicacion.error = "La ubicación o sector es obligatoria"
            edtUbicacion.requestFocus()
            return
        }

        if (tempString.isEmpty()) {
            edtTemperatura.error = "La temperatura es obligatoria"
            edtTemperatura.requestFocus()
            return
        }

        if (humString.isEmpty()) {
            edtHumedad.error = "La humedad es obligatoria"
            edtHumedad.requestFocus()
            return
        }

        // 2. Validación de conversión numérica
        val temperatura = tempString.toDoubleOrNull()
        if (temperatura == null) {
            edtTemperatura.error = "Ingresa un número decimal válido"
            edtTemperatura.requestFocus()
            return
        }

        val humedad = humString.toDoubleOrNull()
        if (humedad == null || humedad < 0.0 || humedad > 100.0) {
            edtHumedad.error = "Ingresa una humedad válida entre 0% y 100%"
            edtHumedad.requestFocus()
            return
        }

        // 3. Persistencia en la colección 'registros_invernadero' en Firestore
        if (registroIdEditar != null) {
            // MODO EDITAR: Reemplaza los datos del documento existente
            val registroActualizado = RegistroInvernadero(
                id = registroIdEditar!!,
                ubicacion = ubicacion,
                temperatura = temperatura,
                humedad = humedad
            )

            db.collection("registros_invernadero")
                .document(registroIdEditar!!)
                .set(registroActualizado)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registro actualizado correctamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al actualizar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            // MODO CREAR: Añade un nuevo documento
            val nuevoRegistro = RegistroInvernadero(
                ubicacion = ubicacion,
                temperatura = temperatura,
                humedad = humedad
            )

            db.collection("registros_invernadero")
                .add(nuevoRegistro)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registro guardado exitosamente", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
