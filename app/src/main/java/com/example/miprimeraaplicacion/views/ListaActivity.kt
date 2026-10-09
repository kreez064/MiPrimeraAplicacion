package com.example.miprimeraaplicacion.views

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.miprimeraaplicacion.R
import com.example.miprimeraaplicacion.adapters.RegistroInvernaderoAdapter
import com.example.miprimeraaplicacion.models.RegistroInvernadero
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListaActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RegistroInvernaderoAdapter
    private lateinit var rvLecturas: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)

        db = Firebase.firestore

        // Inicialización del adaptador con callbacks para Editar y Eliminar
        adapter = RegistroInvernaderoAdapter(
            onEditarClick = { registroAEditar ->
                // Abre el formulario en Modo Edición con los datos del registro seleccionado
                val intent = Intent(this, FormularioLecturaActivity::class.java)
                intent.putExtra("registroId", registroAEditar.id)
                intent.putExtra("registroUbicacion", registroAEditar.ubicacion)
                intent.putExtra("registroTemperatura", registroAEditar.temperatura)
                intent.putExtra("registroHumedad", registroAEditar.humedad)
                startActivity(intent)
            },
            onEliminarClick = { registroAEliminar ->
                // Muestra un AlertDialog para confirmar antes de eliminar
                mostrarDialogoConfirmacion(registroAEliminar)
            }
        )

        rvLecturas = findViewById(R.id.rvLecturas)
        rvLecturas.layoutManager = LinearLayoutManager(this)
        rvLecturas.adapter = adapter

        // Escuchar datos en tiempo real de la colección 'registros_invernadero'
        escucharRegistrosInvernadero()

        // Botón flotante para agregar un nuevo registro
        val fabAgregar = findViewById<FloatingActionButton>(R.id.fabAgregarLectura)
        fabAgregar.setOnClickListener {
            val intent = Intent(this, FormularioLecturaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun escucharRegistrosInvernadero() {
        db.collection("registros_invernadero")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("ListaActivity", "Error al escuchar registros_invernadero.", error)
                    Toast.makeText(this, "Error al cargar datos del invernadero", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val registros = snapshot.toObjects(RegistroInvernadero::class.java)
                    Log.d("ListaActivity", "Registros recuperados: ${registros.size}")
                    adapter.actualizarLista(registros)
                }
            }
    }

    /**
     * Cuadro de diálogo modal (AlertDialog) para confirmar la eliminación de un registro.
     */
    private fun mostrarDialogoConfirmacion(registro: RegistroInvernadero) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Registro")
        builder.setMessage("¿Estás seguro de que deseas eliminar el registro de ventilación en '${registro.ubicacion}'?")
        
        builder.setPositiveButton("Sí, eliminar") { dialog, _ ->
            eliminarRegistro(registro)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun eliminarRegistro(registro: RegistroInvernadero) {
        db.collection("registros_invernadero")
            .document(registro.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Registro eliminado del sistema", Toast.LENGTH_SHORT).show()
                Log.d("ListaActivity", "Registro ${registro.id} eliminado con éxito.")
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.w("ListaActivity", "Error al eliminar registro", e)
            }
    }
}
