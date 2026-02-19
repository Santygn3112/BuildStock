package com.guillen.buildstock.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.guillen.buildstock.data.model.Movement
import com.guillen.buildstock.data.model.Tool
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class InventoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val toolsCollection = db.collection("tools")
    private val movementsCollection = db.collection("movements")

    // --- GESTIÓN DE HERRAMIENTAS (CRUD) ---

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
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en addTool: ${e.message}", e)
            false
        }
    }

    suspend fun updateTool(tool: Tool): Boolean {
        return try {
            if (tool.id.isEmpty()) return false
            toolsCollection.document(tool.id).set(tool).await()
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en updateTool: ${e.message}", e)
            false
        }
    }

    suspend fun deleteTool(id: String): Boolean {
        return try {
            if (id.isEmpty()) return false
            toolsCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en deleteTool: ${e.message}", e)
            false
        }
    }

    // --- CONSULTAS ---

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

    // Obtener todas las herramientas que tiene un usuario ahora mismo
    suspend fun getToolsByUserId(userId: String): List<Tool> {
        return try {
            val snapshot = toolsCollection.whereEqualTo("currentUserId", userId).get().await()
            snapshot.toObjects(Tool::class.java)
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getToolsByUserId: ${e.message}", e)
            emptyList()
        }
    }

    // NUEVO: Contador de movimientos de un usuario en el día de hoy (Para el Perfil)
    suspend fun getTodayUserMovementsCount(userId: String): Int {
        return try {
            val snapshot = movementsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            // Calculamos el inicio del día a las 00:00 en milisegundos
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            val movements = snapshot.toObjects(Movement::class.java)
            // Filtramos solo los que son de hoy
            movements.count { it.timestamp >= startOfDay }
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getTodayUserMovementsCount: ${e.message}", e)
            0
        }
    }

    // --- LÓGICA DE TRANSACCIONES ---

    suspend fun processPickupTransaction(tools: List<Tool>, userId: String, userName: String): Boolean {
        if (userId.isEmpty()) {
            Log.e("FIREBASE_MONITOR", "Error: userId está vacío en la transacción")
            return false
        }
        return try {
            val batch = db.batch()
            var operationsCount = 0

            for (tool in tools) {
                if (tool.id.isEmpty()) {
                    Log.e("FIREBASE_MONITOR", "Error: La herramienta ${tool.name} tiene ID vacío")
                    continue
                }

                val toolRef = toolsCollection.document(tool.id)
                batch.update(toolRef, mapOf(
                    "status" to "en uso",
                    "currentUserId" to userId,
                    "currentUserName" to userName
                ))

                val movementRef = movementsCollection.document()
                val movement = Movement(
                    id = movementRef.id,
                    toolId = tool.id,
                    toolName = tool.name,
                    userId = userId,
                    userName = userName,
                    type = "RECOGIDA",
                    timestamp = System.currentTimeMillis()
                )
                batch.set(movementRef, movement)
                operationsCount++
            }

            if (operationsCount == 0) return false

            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Fallo en pickup: ${e.message}")
            false
        }
    }

    suspend fun processReturnTransaction(tools: List<Tool>, userId: String, userName: String): Boolean {
        if (userId.isEmpty()) {
            Log.e("FIREBASE_MONITOR", "Error: userId está vacío en la transacción (devolución)")
            return false
        }
        return try {
            val batch = db.batch()
            var operationsCount = 0

            for (tool in tools) {
                if (tool.id.isEmpty()) {
                    Log.e("FIREBASE_MONITOR", "Error: La herramienta ${tool.name} tiene ID vacío (devolución)")
                    continue
                }

                val toolRef = toolsCollection.document(tool.id)

                batch.update(toolRef, mapOf(
                    "status" to "disponible",
                    "currentUserId" to "",
                    "currentUserName" to ""
                ))

                val movementRef = movementsCollection.document()
                val movement = Movement(
                    id = movementRef.id,
                    toolId = tool.id,
                    toolName = tool.name,
                    userId = userId,
                    userName = userName,
                    type = "DEVOLUCION",
                    timestamp = System.currentTimeMillis()
                )
                batch.set(movementRef, movement)
                operationsCount++
            }

            if (operationsCount == 0) return false

            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Fallo en processReturnTransaction: ${e.message}", e)
            false
        }
    }

    // Para la futura sección de "Últimos Movimientos" global
    suspend fun getRecentMovements(limit: Long = 10): List<Movement> {
        return try {
            val snapshot = movementsCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .await()
            snapshot.toObjects(Movement::class.java)
        } catch (e: Exception) {
            Log.e("FIREBASE_MONITOR", "Error en getRecentMovements: ${e.message}", e)
            emptyList()
        }
    }
}