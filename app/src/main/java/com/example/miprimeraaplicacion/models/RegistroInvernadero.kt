package com.example.miprimeraaplicacion.models

import com.google.firebase.firestore.DocumentId

/**
 * Modelo de datos para representar un registro de monitoreo ambiental del invernadero.
 * Mapeado directamente con la colección 'registros_invernadero' en Cloud Firestore.
 */
data class RegistroInvernadero(
    @DocumentId
    var id: String = "",
    var ubicacion: String = "",
    var temperatura: Double = 0.0,
    var humedad: Double = 0.0
)
