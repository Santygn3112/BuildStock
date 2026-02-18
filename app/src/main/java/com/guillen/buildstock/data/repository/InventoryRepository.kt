package com.guillen.buildstock.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.guillen.buildstock.data.model.Loan
import com.guillen.buildstock.data.model.Tool
import com.guillen.buildstock.ui.cart.CartItem
import kotlinx.coroutines.tasks.await

class InventoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val toolsCollection = db.collection("tools")
    private val loansCollection = db.collection("loans")

    // --- FUNCIONES DE GESTIÓN DE HERRAMIENTAS (Restauradas y Completas) ---

    suspend fun getToolById(id: String): Tool? {
        return try {
            val snapshot = toolsCollection.document(id).get().await()
            snapshot.toObject(Tool::class.java)
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getToolById: ${e.message}", e)
            null
        }
    }

    suspend fun addTool(tool: Tool): Boolean {
        return try {
            toolsCollection.add(tool).await()
            Log.d("FIREBASE_MONITOR", "addTool: Éxito para ${tool.name}")
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en addTool: ${e.message}", e)
            false
        }
    }

    suspend fun updateTool(tool: Tool): Boolean {
        return try {
            if (tool.id.isEmpty()) {
                throw IllegalArgumentException("El ID de la herramienta no puede estar vacío para actualizar.")
            }
            toolsCollection.document(tool.id).set(tool).await()
            Log.d("FIREBASE_MONITOR", "updateTool: Éxito para ${tool.name}")
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en updateTool: ${e.message}", e)
            false
        }
    }
    
    suspend fun deleteTool(id: String): Boolean {
        return try {
            toolsCollection.document(id).delete().await()
            Log.d("FIREBASE_MONITOR", "deleteTool: Éxito para el ID $id")
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en deleteTool: ${e.message}", e)
            false
        }
    }

    suspend fun getToolsCountByStatus(status: String): Int {
        return try {
            val snapshot = toolsCollection.whereEqualTo("status", status).get().await()
            snapshot.size()
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getToolsCountByStatus: ${e.message}", e)
            0
        }
    }

    suspend fun getToolsList(): List<Tool> {
        return try {
            val snapshot = toolsCollection.get().await()
            snapshot.toObjects(Tool::class.java)
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getToolsList: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getToolsByCategory(category: String): List<Tool> {
        return try {
            val snapshot = toolsCollection.whereEqualTo("category", category).get().await()
            snapshot.toObjects(Tool::class.java)
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getToolsByCategory: ${e.message}", e)
            emptyList()
        }
    }

    // --- LÓGICA DE TRANSACCIONES Y PRÉSTAMOS ---

    suspend fun getActiveLoansByUser(userName: String): List<Loan> {
        return try {
            val snapshot = loansCollection.whereEqualTo("userName", userName).get().await()
            snapshot.toObjects(Loan::class.java)
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getActiveLoansByUser: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun processPickupTransaction(items: List<CartItem>, userId: String, userName: String): Boolean {
        return try {
            val batch = db.batch()
            for (item in items) {
                val toolRef = toolsCollection.document(item.tool.id)
                val stockChange = -item.quantity.toLong()
                batch.update(toolRef, "stock", FieldValue.increment(stockChange))

                val loanRef = loansCollection.document()
                val newLoan = Loan(
                    id = loanRef.id,
                    toolId = item.tool.id,
                    toolName = item.tool.name,
                    userId = userId,
                    userName = userName,
                    quantity = item.quantity
                )
                batch.set(loanRef, newLoan)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Fallo en processPickupTransaction: ${e.message}", e)
            false
        }
    }

    suspend fun processReturnTransaction(loans: List<Loan>): Boolean {
        return try {
            val batch = db.batch()
            for (loan in loans) {
                val toolRef = toolsCollection.document(loan.toolId)
                val stockChange = loan.quantity.toLong()
                batch.update(toolRef, "stock", FieldValue.increment(stockChange))

                val loanRef = loansCollection.document(loan.id)
                batch.delete(loanRef)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Fallo en processReturnTransaction: ${e.message}", e)
            false
        }
    }
}