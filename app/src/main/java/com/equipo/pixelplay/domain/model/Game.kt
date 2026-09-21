package com.equipo.pixelplay.domain.model

/**
 * Modelo de dominio de un videojuego.
 * FreeToGame no expone calificación, por eso el modelo no lleva rating.
 * [description] solo llega en el detalle, por eso es nullable.
 */
data class Game(
    val id: Int,
    val name: String,
    val genre: String,
    val platform: String,
    val thumbnail: String,
    val shortDescription: String,
    val description: String?
)
