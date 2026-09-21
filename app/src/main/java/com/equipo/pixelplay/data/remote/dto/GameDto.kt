package com.equipo.pixelplay.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO único para la API de FreeToGame.
 * La lista (GET games) y el detalle (GET game?id=) comparten estructura;
 * los campos exclusivos del detalle se declaran nullable para que Gson
 * los deje en null cuando se parsea un elemento de la lista.
 */
data class GameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("short_description") val shortDescription: String,
    @SerializedName("genre") val genre: String,
    @SerializedName("platform") val platform: String,
    // Solo presente en el detalle:
    @SerializedName("description") val description: String? = null
)
