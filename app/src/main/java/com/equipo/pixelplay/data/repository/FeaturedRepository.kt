package com.equipo.pixelplay.data.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Único punto de acceso a la configuración de juegos destacados en Cloud Firestore.
 *
 * Modelo:
 * - config/home → { featuredIds: [Int, ...] }   (un solo doc; ids de juegos destacados)
 *
 * Lectura para todos (sección "Destacados" del Home); la escritura la restringen las
 * reglas a admin (allow write: if isAdmin()). Un fallo de Firestore se loguea, nunca
 * tumba la app (misma filosofía que Favoritos/Fase 1).
 */
class FeaturedRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun configDoc() = db.collection("config").document("home")

    /**
     * Ids de los juegos destacados, en vivo. Doc inexistente, campo ausente o error
     * → conjunto vacío. Firestore devuelve los números como Long: se mapean a Int.
     */
    fun observarDestacados(): Flow<Set<Int>> = callbackFlow {
        val registration = configDoc().addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w(TAG, "Error observando destacados", error)
                trySend(emptySet())
                return@addSnapshotListener
            }
            val ids = (snapshot?.get(FIELD_FEATURED) as? List<*>)
                .orEmpty()
                .mapNotNull { (it as? Number)?.toInt() }
                .toSet()
            trySend(ids)
        }
        awaitClose { registration.remove() }
    }

    /**
     * Marca o desmarca un juego como destacado sobre config/home:
     * - Destacar → arrayUnion(gameId).
     * - Quitar   → arrayRemove(gameId).
     *
     * Se usa set(..., SetOptions.merge()) para crear el doc si no existe.
     * [destacadoActual] indica el estado presente para decidir la operación.
     * Un fallo de Firestore se loguea y no propaga (app intacta).
     */
    suspend fun alternarDestacado(gameId: Int, destacadoActual: Boolean) {
        val cambio = if (destacadoActual) {
            FieldValue.arrayRemove(gameId)
        } else {
            FieldValue.arrayUnion(gameId)
        }
        try {
            configDoc()
                .set(mapOf(FIELD_FEATURED to cambio), SetOptions.merge())
                .await()
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo alternar destacado $gameId (app intacta)", e)
        }
    }

    private companion object {
        const val TAG = "PixelPlay/Featured"
        const val FIELD_FEATURED = "featuredIds"
    }
}
