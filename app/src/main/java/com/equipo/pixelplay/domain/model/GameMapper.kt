package com.equipo.pixelplay.domain.model

import com.equipo.pixelplay.data.remote.dto.GameDto

/**
 * Mapea el DTO de red al modelo de dominio.
 * title -> name, short_description -> shortDescription; el resto es directo.
 */
fun GameDto.toGame(): Game = Game(
    id = id,
    name = title,
    genre = genre,
    platform = platform,
    thumbnail = thumbnail,
    shortDescription = shortDescription,
    description = description
)
