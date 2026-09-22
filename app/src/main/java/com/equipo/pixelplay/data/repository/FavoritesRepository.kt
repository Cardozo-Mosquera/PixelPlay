package com.equipo.pixelplay.data.repository

import android.util.Log
import com.equipo.pixelplay.domain.model.Game
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Único punto de acceso a los favoritos por cuenta en Cloud Firestore.
 *
 * Modelo:
 * - users/{uid}/favorites/{gameId} → { name, thumbnail, genre, addedAt }
 * - gameStats/{gameId}            → { name, favoriteCount }
 *
 * El uid se toma de [auth] dentro del repo; sin sesión, todo es no-op seguro
 * (los Flows emiten vacío/false y las escrituras retornan sin tocar Firestore).
 * Un fallo de Firestore se loguea, nunca tumba la app (misma filosofía que Fase 1).
 */
class FavoritesRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private fun uid(): String? = auth.currentUser?.uid

    private fun favoritesCollection(uid: String) =
        db.collection("users").document(uid).collection("favorites")

    /**
     * Favoritos del usuario actual, ordenados por addedAt desc, actualizados en vivo.
     * Sin sesión emite lista vacía.
     *
     * Nota de mapeo: el doc guarda una copia mínima ({ name, thumbnail, genre }).
     * Los campos que Game exige pero no persistimos (platform, shortDescription,
     * description) se rellenan con "" / null: alcanza para pintar GameCard y para
     * navegar a detail/{id}, que recarga el juego completo desde la API.
     */
    fun observarFavoritos(): Flow<List<Game>> {
        val uid = uid() ?: return flowOf(emptyList())
        return callbackFlow {
            val registration = favoritesCollection(uid)
                .orderBy("addedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Error observando favoritos", error)
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val games = snapshot?.documents.orEmpty().mapNotNull { doc ->
                        val id = doc.id.toIntOrNull() ?: return@mapNotNull null
                        Game(
                            id = id,
                            name = doc.getString("name").orEmpty(),
                            genre = doc.getString("genre").orEmpty(),
                            platform = "",
                            thumbnail = doc.getString("thumbnail").orEmpty(),
                            shortDescription = "",
                            description = null
                        )
                    }
                    trySend(games)
                }
            awaitClose { registration.remove() }
        }
    }

    /** True mientras el juego esté en favoritos del usuario actual. Sin sesión, false. */
    fun observarEsFavorito(gameId: Int): Flow<Boolean> {
        val uid = uid() ?: return flowOf(false)
        return callbackFlow {
            val registration = favoritesCollection(uid).document(gameId.toString())
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Error observando favorito $gameId", error)
                        trySend(false)
                        return@addSnapshotListener
                    }
                    trySend(snapshot?.exists() == true)
                }
            awaitClose { registration.remove() }
        }
    }

    /**
     * Marca o desmarca un favorito de forma atómica (WriteBatch):
     * - Marcar   → crea favorites/{id} con la copia mínima + addedAt, e incrementa gameStats +1.
     * - Desmarcar→ borra favorites/{id} e incrementa gameStats -1.
     *
     * [esFavoritoActual] indica el estado presente para decidir la operación.
     * Sin sesión es no-op. Un fallo de Firestore se loguea y no propaga.
     */
    suspend fun alternarFavorito(game: Game, esFavoritoActual: Boolean) {
        val uid = uid() ?: return
        val favDoc = favoritesCollection(uid).document(game.id.toString())
        val statsDoc = db.collection("gameStats").document(game.id.toString())
        try {
            val batch = db.batch()
            if (esFavoritoActual) {
                batch.delete(favDoc)
                batch.set(
                    statsDoc,
                    mapOf("favoriteCount" to FieldValue.increment(-1)),
                    SetOptions.merge()
                )
            } else {
                batch.set(
                    favDoc,
                    mapOf(
                        "name" to game.name,
                        "thumbnail" to game.thumbnail,
                        "genre" to game.genre,
                        "addedAt" to FieldValue.serverTimestamp()
                    )
                )
                batch.set(
                    statsDoc,
                    mapOf(
                        "name" to game.name,
                        "favoriteCount" to FieldValue.increment(1)
                    ),
                    SetOptions.merge()
                )
            }
            batch.commit().await()
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo alternar favorito ${game.id} (app intacta)", e)
        }
    }

    private companion object {
        const val TAG = "PixelPlay/Favorites"
    }
}
