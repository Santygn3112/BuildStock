package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class User(
    @DocumentId
    @get:Exclude // Evita conflictos de ID en la colección de usuarios
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "operario",
    val phone: String = ""
)