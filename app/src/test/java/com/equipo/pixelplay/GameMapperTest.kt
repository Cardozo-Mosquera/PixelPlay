package com.equipo.pixelplay

import com.equipo.pixelplay.data.remote.dto.GameDto
import com.equipo.pixelplay.domain.model.toGame
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verifica el mapeo GameDto -> Game sin necesidad de emulador ni red.
 */
class GameMapperTest {

    @Test
    fun `toGame mapea todos los campos incluido el renombrado title y short_description`() {
        val dto = GameDto(
            id = 452,
            title = "Fortnite",
            thumbnail = "https://www.freetogame.com/g/452/thumbnail.jpg",
            shortDescription = "A free-to-play Battle Royale game.",
            genre = "Shooter",
            platform = "Windows",
            description = "Descripción larga solo del detalle."
        )

        val game = dto.toGame()

        assertEquals(452, game.id)
        assertEquals("Fortnite", game.name)                       // title -> name
        assertEquals("Shooter", game.genre)
        assertEquals("Windows", game.platform)
        assertEquals("https://www.freetogame.com/g/452/thumbnail.jpg", game.thumbnail)
        assertEquals("A free-to-play Battle Royale game.", game.shortDescription) // short_description
        assertEquals("Descripción larga solo del detalle.", game.description)
    }

    @Test
    fun `toGame deja description en null cuando el DTO no la trae (caso lista)`() {
        val dto = GameDto(
            id = 1,
            title = "Dauntless",
            thumbnail = "https://www.freetogame.com/g/1/thumbnail.jpg",
            shortDescription = "A free-to-play action RPG.",
            genre = "MMORPG",
            platform = "Windows"
        )

        val game = dto.toGame()

        assertEquals(null, game.description)
    }
}
