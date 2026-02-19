package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Movement(
    @DocumentId
    @get:Exclude
    val id: String = "",
    val toolId: String = "",
    val toolName: String = "",
    val userId: String = "",
    val userName: String = "",
    val type: String = "", // Aquí guardaremos "RECOGIDA" o "DEVOLUCION"
    val timestamp: Long = System.currentTimeMillis()
)