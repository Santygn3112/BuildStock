package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Tool(
    @DocumentId
    @get:Exclude
    val id: String = "",
    val name: String = "",
    val brandModel: String = "",
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val status: String = "disponible",
    // Nuevos campos para tracking directo:
    val currentUserId: String = "",
    val currentUserName: String = ""
)