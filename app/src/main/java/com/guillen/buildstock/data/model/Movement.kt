package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

// Registro de un movimiento de herramienta
data class Movement(
    // Identificador único del movimiento
    @DocumentId
    val id: String = "",
    val toolId: String = "",
    val toolName: String = "",
    val userId: String = "",
    val userName: String = "",
    // Tipo de movimiento realizado
    val type: String = "",
    // Marca de tiempo del movimiento en milisegundos
    val timestamp: Long = System.currentTimeMillis()
)
