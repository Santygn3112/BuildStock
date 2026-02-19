package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

data class Movement(
    @DocumentId
    val id: String = "",
    val toolId: String = "",
    val toolName: String = "",
    val userId: String = "",
    val userName: String = "",
    val type: String = "",
    val timestamp: Long = System.currentTimeMillis()
)