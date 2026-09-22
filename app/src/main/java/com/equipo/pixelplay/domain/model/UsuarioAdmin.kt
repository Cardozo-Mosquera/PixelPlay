package com.equipo.pixelplay.domain.model

import com.google.firebase.Timestamp

/**
 * Vista de un usuario registrado para el panel de administración.
 * [createdAt] es nullable: puede faltar en docs antiguos o recién creados
 * (serverTimestamp aún no resuelto).
 */
data class UsuarioAdmin(
    val uid: String,
    val email: String,
    val role: String,
    val createdAt: Timestamp?
)
