package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

data class Tool(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val brandModel: String = "",
    val category: String = "",
    val location: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val status: String = "disponible",
    val currentUserId: String = "",
    val currentUserName: String = ""
)