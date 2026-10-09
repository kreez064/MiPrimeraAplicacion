package com.example.miprimeraaplicacion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miprimeraaplicacion.R
import com.example.miprimeraaplicacion.models.RegistroInvernadero

/**
 * Adaptador para RecyclerView que administra y muestra la lista de mediciones ambientales del invernadero.
 */
class RegistroInvernaderoAdapter(
    private var listaRegistros: List<RegistroInvernadero> = listOf(),
    private val onEditarClick: (RegistroInvernadero) -> Unit,
    private val onEliminarClick: (RegistroInvernadero) -> Unit
) : RecyclerView.Adapter<RegistroInvernaderoAdapter.RegistroViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistroViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registro_invernadero, parent, false)
        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistroViewHolder, position: Int) {
        val registro = listaRegistros[position]
        holder.bind(registro, onEditarClick, onEliminarClick)
    }

    override fun getItemCount(): Int = listaRegistros.size

    fun actualizarLista(nuevaLista: List<RegistroInvernadero>) {
        listaRegistros = nuevaLista
        notifyDataSetChanged()
    }

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtUbicacion: TextView = itemView.findViewById(R.id.txtUbicacion)
        private val txtMetricas: TextView = itemView.findViewById(R.id.txtMetricas)
        private val txtEstadoVentilacion: TextView = itemView.findViewById(R.id.txtEstadoVentilacion)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminar)

        fun bind(
            registro: RegistroInvernadero,
            onEditarClick: (RegistroInvernadero) -> Unit,
            onEliminarClick: (RegistroInvernadero) -> Unit
        ) {
            // Rotulado con lenguaje de negocio
            txtUbicacion.text = registro.ubicacion
            txtMetricas.text = "Humedad: ${registro.humedad}% - Temp: ${registro.temperatura}°C"

            // Lógica de negocio visual para ventilación según nivel de humedad
            if (registro.humedad >= 70.0) {
                txtEstadoVentilacion.text = "⚠️ Ventilación requerida (Humedad alta)"
                txtEstadoVentilacion.setTextColor(itemView.context.getColor(android.R.color.holo_red_dark))
            } else {
                txtEstadoVentilacion.text = "✅ Ventilación normal (Humedad controlada)"
                txtEstadoVentilacion.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
            }

            // Click en la fila para abrir en modo edición
            itemView.setOnClickListener {
                onEditarClick(registro)
            }

            // Click en el botón de basurero para solicitar confirmación de eliminación
            btnEliminar.setOnClickListener {
                onEliminarClick(registro)
            }
        }
    }
}
