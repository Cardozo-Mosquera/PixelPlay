package com.equipo.pixelplay.data.repository

import com.equipo.pixelplay.domain.model.EstadisticaJuego
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Acceso de solo lectura a estadísticas para el panel de administración (Fase C).
 * Mismo patrón que [AdminRepository]: suspend + .await(); la excepción se propaga y
 * el ViewModel la traduce a Error. No escribe nada.
 */
class StatsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Total de usuarios registrados = cantidad de docs en `users`.
     * Usa una count aggregation (no descarga los docs, solo el conteo del servidor).
     */
    suspend fun contarUsuarios(): Int {
        val snapshot = db.collection("users")
            .count()
            .get(AggregateSource.SERVER)
            .await()
        return snapshot.count.toInt()
    }

    /**
     * Juego más marcado como favorito: gameStats ordenado por favoriteCount desc, tomando 1.
     * Excluye favoriteCount <= 0 (juegos sin favoritos reales). null si no hay ninguno.
     */
    suspend fun juegoMasMarcado(): EstadisticaJuego? {
        val snapshot = db.collection("gameStats")
            .orderBy("favoriteCount", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()
        val doc = snapshot.documents.firstOrNull() ?: return null
        val gameId = doc.id.toIntOrNull() ?: return null
        val count = (doc.getLong("favoriteCount") ?: 0L).toInt()
        if (count <= 0) return null
        return EstadisticaJuego(
            gameId = gameId,
            name = doc.getString("name").orEmpty(),
            favoriteCount = count
        )
    }
}
