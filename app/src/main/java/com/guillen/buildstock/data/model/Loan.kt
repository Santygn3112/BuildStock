
package com.guillen.buildstock.data.model

import com.google.firebase.firestore.DocumentId

data class Loan(
    @DocumentId
    val id: String = "",
    val toolId: String = "",
    val toolName: String = "",
    val userId: String = "",
    val userName: String = "",
    val quantity: Int = 0
)
