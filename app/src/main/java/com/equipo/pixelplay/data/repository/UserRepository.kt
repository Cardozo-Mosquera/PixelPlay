package com.equipo.pixelplay.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Único punto de acceso a los documentos de usuario en Cloud Firestore.
 * Colección `users`, un documento por uid: { email, role, createdAt }.
 * Ningún @Composable ni ViewModel toca Firestore directo: todo pasa por aquí.
 */
class UserRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    /**
     * Crea o actualiza `users/{uid}` de forma idempotente.
     *
     * - Si el doc NO existe: lo crea con { email, role: "user", createdAt: serverTimestamp }.
     * - Si el doc YA existe: mergea SOLO el email; nunca toca `role` ni `createdAt`,
     *   así un role="admin" existente jamás se degrada y se conserva el createdAt original.
     *
     * Sirve también de backfill para cuentas creadas antes de Firestore.
     */
    suspend fun ensureUserDoc(uid: String, email: String) {
        val docRef = db.collection("users").document(uid)
        val snapshot = docRef.get().await()
        if (snapshot.exists()) {
            // Doc ya presente: solo refrescamos email, sin pisar role ni createdAt.
            docRef.set(mapOf("email" to email), SetOptions.merge()).await()
        } else {
            val data = mapOf(
                "email" to email,
                "role" to "user",
                "createdAt" to FieldValue.serverTimestamp()
            )
            docRef.set(data).await()
        }
    }

    /** Devuelve el role de `users/{uid}`, o "user" si el doc o el campo no existen. */
    suspend fun getUserRole(uid: String): String {
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.getString("role") ?: "user"
    }

    /** Rol del usuario autenticado actual, o "user" si no hay sesión. */
    suspend fun rolUsuarioActual(): String {
        val uid = auth.currentUser?.uid ?: return "user"
        return getUserRole(uid)
    }
}
