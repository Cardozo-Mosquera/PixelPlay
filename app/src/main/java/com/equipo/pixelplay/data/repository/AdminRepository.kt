package com.equipo.pixelplay.data.repository

import com.equipo.pixelplay.domain.model.UsuarioAdmin
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Acceso de solo lectura a la colección `users` para el panel de administración.
 * Fase 3 (Admin A): solo listar. Ascender/degradar rol llegará con las reglas de seguridad.
 */
class AdminRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Lista todos los usuarios, ordenados por createdAt desc (los sin createdAt al final).
     *
     * Se ordena en cliente y no con orderBy en la query a propósito: Firestore excluye
     * de un orderBy los docs que no tengan el campo, y no queremos perder usuarios sin createdAt.
     */
    suspend fun listarUsuarios(): List<UsuarioAdmin> {
        val snapshot = db.collection("users").get().await()
        return snapshot.documents
            .map { doc ->
                UsuarioAdmin(
                    uid = doc.id,
                    email = doc.getString("email").orEmpty(),
                    role = doc.getString("role") ?: "user",
                    createdAt = doc.getTimestamp("createdAt")
                )
            }
            .sortedWith(
                compareByDescending(nullsFirst()) { it.createdAt }
            )
    }
}
